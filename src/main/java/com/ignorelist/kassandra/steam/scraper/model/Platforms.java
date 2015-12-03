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
	"windows",
	"mac",
	"linux"
})
public class Platforms {

	@JsonProperty("windows")
	private Boolean windows;
	@JsonProperty("mac")
	private Boolean mac;
	@JsonProperty("linux")
	private Boolean linux;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The windows
	 */
	@JsonProperty("windows")
	public Boolean getWindows() {
		return windows;
	}

	/**
	 *
	 * @param windows The windows
	 */
	@JsonProperty("windows")
	public void setWindows(Boolean windows) {
		this.windows=windows;
	}

	/**
	 *
	 * @return The mac
	 */
	@JsonProperty("mac")
	public Boolean getMac() {
		return mac;
	}

	/**
	 *
	 * @param mac The mac
	 */
	@JsonProperty("mac")
	public void setMac(Boolean mac) {
		this.mac=mac;
	}

	/**
	 *
	 * @return The linux
	 */
	@JsonProperty("linux")
	public Boolean getLinux() {
		return linux;
	}

	/**
	 *
	 * @param linux The linux
	 */
	@JsonProperty("linux")
	public void setLinux(Boolean linux) {
		this.linux=linux;
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
		return new HashCodeBuilder().append(windows).append(mac).append(linux).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Platforms)==false) {
			return false;
		}
		Platforms rhs=((Platforms) other);
		return new EqualsBuilder().append(windows, rhs.windows).append(mac, rhs.mac).append(linux, rhs.linux).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
