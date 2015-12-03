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
	"coming_soon",
	"date"
})
public class ReleaseDate {

	@JsonProperty("coming_soon")
	private Boolean comingSoon;
	@JsonProperty("date")
	private String date;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The comingSoon
	 */
	@JsonProperty("coming_soon")
	public Boolean getComingSoon() {
		return comingSoon;
	}

	/**
	 *
	 * @param comingSoon The coming_soon
	 */
	@JsonProperty("coming_soon")
	public void setComingSoon(Boolean comingSoon) {
		this.comingSoon=comingSoon;
	}

	/**
	 *
	 * @return The date
	 */
	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	/**
	 *
	 * @param date The date
	 */
	@JsonProperty("date")
	public void setDate(String date) {
		this.date=date;
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
		return new HashCodeBuilder().append(comingSoon).append(date).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof ReleaseDate)==false) {
			return false;
		}
		ReleaseDate rhs=((ReleaseDate) other);
		return new EqualsBuilder().append(comingSoon, rhs.comingSoon).append(date, rhs.date).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
