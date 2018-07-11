package xyz.cleangone.data.aws.dynamo.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import xyz.cleangone.data.aws.AwsClientFactory;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;

import java.util.HashMap;
import java.util.Map;

public class DynamoBaseDao<T extends BaseEntity>
{
    protected DynamoDBMapper mapper;

    public DynamoBaseDao()
    {
        AwsClientFactory awsClientFactory = AwsClientFactory.getInstance();

        AmazonDynamoDB dynamoDB = awsClientFactory.createDynamoClient();

        mapper = new DynamoDBMapper(dynamoDB, awsClientFactory.getCredentialsProvider());
    }

    public void save(T object)
    {
        if (object.getId() == null) { object.setCreatedDate(); }

        object.setUpdatedDate();
        mapper.save(object);
    }

    public void delete(T object)
    {
        mapper.delete(object);
    }

    public S3Link createS3Link(String filePath)
    {
        return mapper.createS3Link(AwsClientFactory.getRegionName(), AwsClientFactory.getBucketName(), filePath);
    }

    public DynamoDBScanExpression buildEqualsScanExpression(String name, String value)
    {
        return buildScanExpression(name + " = :val1", value);
    }

    public DynamoDBScanExpression buildContainsScanExpression(String name, String value)
    {
        return buildScanExpression("contains (" + name + ", :val1)", value);
    }

    private DynamoDBScanExpression buildScanExpression(String filter, String value)
    {
        Map<String, AttributeValue> eav = createEav(":val1", value);
        return new DynamoDBScanExpression()
            .withFilterExpression(filter)
            .withExpressionAttributeValues(eav);
    }

    protected Map<String, AttributeValue> createEav(String key, String value)
    {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(key, new AttributeValue().withS(value));

        return eav;
    }
}



