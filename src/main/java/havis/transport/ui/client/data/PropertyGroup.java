package havis.transport.ui.client.data;

import java.util.List;

public class PropertyGroup {
	private String name;
	private List<Property> properties;
	
	public PropertyGroup(String groupName, List<Property> properties) {
		this.name = groupName;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}

	public List<Property> getProperties() {
		return properties;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyGroup other = (PropertyGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PropertyGroup [groupName=" + name + ", properties=" + properties + "]";
	}
}
