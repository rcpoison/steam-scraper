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
	"id",
	"description"
})
public class Category {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("description")
	private String description;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The id
	 */
	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	/**
	 *
	 * @param id The id
	 */
	@JsonProperty("id")
	public void setId(Integer id) {
		this.id=id;
	}

	/**
	 *
	 * @return The description
	 */
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @param description The description
	 */
	@JsonProperty("description")
	public void setDescription(String description) {
		this.description=description;
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
		return new HashCodeBuilder().append(id).append(description).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Category)==false) {
			return false;
		}
		Category rhs=((Category) other);
		return new EqualsBuilder().append(id, rhs.id).append(description, rhs.description).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
