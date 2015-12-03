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
	"minimum",
	"recommended"
})
public class Requirement {

	@JsonProperty("minimum")
	private String minimum;
	@JsonProperty("recommended")
	private String recommended;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The minimum
	 */
	@JsonProperty("minimum")
	public String getMinimum() {
		return minimum;
	}

	/**
	 *
	 * @param minimum The minimum
	 */
	@JsonProperty("minimum")
	public void setMinimum(String minimum) {
		this.minimum=minimum;
	}

	/**
	 *
	 * @return The recommended
	 */
	@JsonProperty("recommended")
	public String getRecommended() {
		return recommended;
	}

	/**
	 *
	 * @param recommended The recommended
	 */
	@JsonProperty("recommended")
	public void setRecommended(String recommended) {
		this.recommended=recommended;
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
		return new HashCodeBuilder().append(minimum).append(recommended).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Requirement)==false) {
			return false;
		}
		Requirement rhs=((Requirement) other);
		return new EqualsBuilder().append(minimum, rhs.minimum).append(recommended, rhs.recommended).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
