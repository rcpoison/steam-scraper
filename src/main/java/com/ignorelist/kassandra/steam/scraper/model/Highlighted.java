package com.ignorelist.kassandra.steam.scraper.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"name",
	"path"
})
public class Highlighted {

	@JsonProperty("name")
	private String name;
	@JsonProperty("path")
	private String path;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name=name;
	}

	/**
	 *
	 * @return The path
	 */
	@JsonProperty("path")
	public String getPath() {
		return path;
	}

	/**
	 *
	 * @param path The path
	 */
	@JsonProperty("path")
	public void setPath(String path) {
		this.path=path;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(path).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Highlighted)==false) {
			return false;
		}
		Highlighted rhs=((Highlighted) other);
		return new EqualsBuilder().append(name, rhs.name).append(path, rhs.path).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
