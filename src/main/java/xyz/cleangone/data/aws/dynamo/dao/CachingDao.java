package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

public class CachingDao<T extends BaseEntity> extends DynamoBaseDao<T>
{
    private EntityLastTouchedDao entityLastTouchedDao = new EntityLastTouchedDao();

    public EntityLastTouched getEntityLastTouched(String entityId)
    {
        return entityLastTouchedDao.get(entityId);
    }
    public void setEntityLastTouched(String entityId, EntityType entityType)
    {
        entityLastTouchedDao.set(entityId, entityType);
    }
}



