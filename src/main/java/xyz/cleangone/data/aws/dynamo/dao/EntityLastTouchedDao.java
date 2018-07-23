package xyz.cleangone.data.aws.dynamo.dao;

import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

public class EntityLastTouchedDao extends DynamoBaseDao<EntityLastTouched>
{
    public EntityLastTouched get(String entityId)
    {
        // look up the lastTouch by the primary id, which is the entityId
        return mapper.load(EntityLastTouched.class, entityId);
    }

    public void set(String entityId, EntityType entityType)
    {
        EntityLastTouched lastTouched = get(entityId);
        if (lastTouched == null) { lastTouched = new EntityLastTouched(entityId); }

        lastTouched.setTouchDate(entityType);
        mapper.save(lastTouched);
    }
}



