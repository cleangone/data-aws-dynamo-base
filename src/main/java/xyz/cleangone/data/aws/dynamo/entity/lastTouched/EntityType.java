package xyz.cleangone.data.aws.dynamo.entity.lastTouched;

public class EntityType
{
    public static EntityType ENTITY    = new EntityType("Entity");
    public static EntityType EVENT     = new EntityType("Event");
    public static EntityType PERSON    = new EntityType("Person");
    public static EntityType ITEM      = new EntityType("Item");
    public static EntityType BID       = new EntityType("Bid");
    public static EntityType USER      = new EntityType("User");
    public static EntityType TAG       = new EntityType("Tag");
    public static EntityType TAGTYPE   = new EntityType("TagType");
    public static EntityType PERSONTAG = new EntityType("PersonTag");
    public static EntityType CATEGORY  = new EntityType("Category");
    public static EntityType PARTICIPANT = new EntityType("Participant");
    public static EntityType EVENTDATE = new EntityType("EventDate");
    public static EntityType ACTION    = new EntityType("Action");

    private String name;

    public EntityType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}