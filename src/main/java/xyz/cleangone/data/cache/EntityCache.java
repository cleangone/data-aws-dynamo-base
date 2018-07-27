package xyz.cleangone.data.cache;

import xyz.cleangone.data.aws.dynamo.dao.EntityLastTouchedDao;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseEntity;
import xyz.cleangone.data.aws.dynamo.entity.base.BaseNamedEntity;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityLastTouched;
import xyz.cleangone.data.aws.dynamo.entity.lastTouched.EntityType;

import java.util.*;
import java.util.stream.Collectors;

// cache of a particular kind of entity by a parent/reference id
public class EntityCache <T extends BaseEntity>
{
    private static final EntityLastTouchedDao DAO = new EntityLastTouchedDao();

    private final Map<String, EntityCacheItem<T>> entityIdToEntityCacheItems = new HashMap<>();
    private final Map<String, Date> entityIdLastChecked = new HashMap<>();
    private final List<String> entityIds = new ArrayList<>();
    private final Map<String, EntityCacheStat> entityIdToStat = new HashMap<>();

    private final EntityType entityType;
    private final Integer maxEntities;
    private final long bypassTime;

    public EntityCache(EntityType entityType)
    {
        this(entityType, null);
    }
    public EntityCache(EntityType entityType, Integer maxEntities)
    {
        this(entityType, maxEntities, 1000);
    }
    public EntityCache(EntityType entityType, Integer maxEntities, long bypassTime)
    {
        this.entityType = entityType;
        this.maxEntities = maxEntities;
        this.bypassTime = bypassTime;
    }

    public void clear(BaseNamedEntity keyEntity) { clear(keyEntity.getId()); }
    public void clear(String keyEntityId)
    {
        entityIdToEntityCacheItems.put(keyEntityId, null);
        entityIdLastChecked.put(keyEntityId, null);
        entityIds.remove(keyEntityId);
    }

    public List<T> get(String keyEntityId)
    {
        return get(keyEntityId, null, null);
    }
    public List<T> get(BaseNamedEntity keyEntity, String orgId)
    {
        return get(keyEntity.getId(), keyEntity.getName(), orgId);
    }

    // optional orgId used for cacheStats
    public List<T> get(String keyEntityId, String keyEntityName, String orgId)
    {
        Date start = new Date();
        EntityCacheItem <T> entityCacheItem = entityIdToEntityCacheItems.get(keyEntityId);
        if (entityCacheItem == null)
        {
            return null;
        }

        Date lastChecked = entityIdLastChecked.get(keyEntityId);
        if (lastChecked != null && lastChecked.getTime() + bypassTime > start.getTime())
        {
            // checked lastTouch within the bypassTime
            hit(keyEntityId, keyEntityName, orgId, start);
            return entityCacheItem.getEntities();
        }

        EntityLastTouched lastTouch = DAO.get(keyEntityId);
        entityIdLastChecked.put(keyEntityId, start);
        if (lastTouch != null &&
            lastTouch.touchedBefore(entityType, entityCacheItem.getCacheDate()))
        {
            hit(keyEntityId, keyEntityName, orgId, start);
            return entityCacheItem.getEntities();
        }

        entityIdToEntityCacheItems.remove(keyEntityId);
        return null;
    }

    public void put(String keyEntityId, List<T> entities, Date start)
    {
        put(keyEntityId, entities, start, null, null);
    }
    public void put(BaseNamedEntity keyEntity, List<T> entities, String orgId, Date start)
    {
        put(keyEntity.getId(), entities, start, keyEntity.getName(), orgId);
    }

    public void put(String keyEntityId, List<T> entities, Date start, String keyEntityName, String orgId)
    {
        entityIdToEntityCacheItems.put(keyEntityId, new EntityCacheItem<>(entities));
        miss(keyEntityId, keyEntityName, orgId, start);

        if (maxEntities != null)
        {
            // todo - make this a more applicable collection, like a stack
            // beginning of list is most recent
            entityIds.remove(keyEntityId);
            entityIds.add(0, keyEntityId);

            if (entityIds.size() > maxEntities)
            {
                String entityIdToRemove = entityIds.remove(entityIds.size() - 1);
                entityIdToEntityCacheItems.remove(entityIdToRemove);
            }
        }
    }

    public List<EntityCacheStat> getCacheStats(String orgId)
    {
        return entityIdToStat.values().stream()
            .filter(s -> s.getOrgId().equals(orgId))
            .collect(Collectors.toList());
    }

    private void hit(String keyEntityId, String keyEntityName, String orgId, Date start)
    {
        if (keyEntityName != null) { getStat(keyEntityId, keyEntityName, orgId).hit(start); }
    }

    private void miss(String keyEntityId, String keyEntityName, String orgId, Date start)
    {
        if (keyEntityName != null) { getStat(keyEntityId, keyEntityName, orgId).miss(start); }
    }

    private EntityCacheStat getStat(String keyEntityId, String keyEntityName, String orgId)
    {
        EntityCacheStat stat = entityIdToStat.get(keyEntityId);
        if (stat == null)
        {
            stat = new EntityCacheStat(keyEntityName, entityType, orgId);
            entityIdToStat.put(keyEntityId, stat);
        }

        return stat;
    }

    class EntityCacheItem <T extends BaseEntity>
    {
        private Date cacheDate;
        private List<T> entities;

        EntityCacheItem(List<T> entities)
        {
            cacheDate = new Date();
            this.entities = entities;
        }

        Date getCacheDate()
        {
            return cacheDate;
        }
        List<T> getEntities()
        {
            return entities;
        }
    }
}
