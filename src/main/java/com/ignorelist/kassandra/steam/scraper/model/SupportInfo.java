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
	"url",
	"email"
})
public class SupportInfo {

	@JsonProperty("url")
	private String url;
	@JsonProperty("email")
	private String email;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The url
	 */
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	/**
	 *
	 * @param url The url
	 */
	@JsonProperty("url")
	public void setUrl(String url) {
		this.url=url;
	}

	/**
	 *
	 * @return The email
	 */
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	/**
	 *
	 * @param email The email
	 */
	@JsonProperty("email")
	public void setEmail(String email) {
		this.email=email;
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
		return new HashCodeBuilder().append(url).append(email).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof SupportInfo)==false) {
			return false;
		}
		SupportInfo rhs=((SupportInfo) other);
		return new EqualsBuilder().append(url, rhs.url).append(email, rhs.email).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
