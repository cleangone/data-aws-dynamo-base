package xyz.cleangone.data.aws.dynamo.entity.base;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Set;

@DynamoDBTable(tableName = "NamedDummy")
public class BaseNamedEntity extends BaseEntity
{
    public static final EntityField NAME_FIELD = new EntityField("baseNamed.name", "Name");
    public static final EntityField ENABLED_FIELD = new EntityField("baseNamed.enabled", "Enabled");
    public static final EntityField NOTES_FIELD = new EntityField("baseNamed.notes", "Notes");

    protected String name;
    protected boolean enabled;
    protected String notes;

    public BaseNamedEntity()
    {
        super();
    }

    public BaseNamedEntity(String name)
    {
        this();
        setName(name);
    }

    public String getEnabledString()
    {
        return enabled ? "Enabled" : "Disabled";
    }

    public String get(EntityField field)
    {
        if (NAME_FIELD.equals(field)) { return getName(); }
        else if (NOTES_FIELD.equals(field)) { return getNotes(); }
        else return super.get(field);
    }

    public void set(EntityField field, String value)
    {
        if (NAME_FIELD.equals(field)) { setName(value); }
        else if (NOTES_FIELD.equals(field)) { setNotes(value); }
        else super.set(field, value);
    }

    public boolean getBoolean(EntityField field)
    {
        if (ENABLED_FIELD.equals(field)) return getEnabled();
        else return super.getBoolean(field);
    }

    public void setBoolean(EntityField field, boolean value)
    {
        if (ENABLED_FIELD.equals(field)) setEnabled(value);
        else super.setBoolean(field, value);
    }

    @DynamoDBAttribute(attributeName="Name")
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName="Enabled")
    public boolean getEnabled()
    {
        return enabled;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @DynamoDBAttribute(attributeName="Notes")
    public String getNotes()
    {
        return notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BaseNamedEntity)) return false;
        if (!super.equals(o)) return false;

        BaseNamedEntity that = (BaseNamedEntity) o;

        if (getEnabled() != that.getEnabled()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getNotes() != null ? getNotes().equals(that.getNotes()) : that.getNotes() == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEnabled() ? 1 : 0);
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        return result;
    }
}



