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
	"path_thumbnail",
	"path_full"
})
public class Screenshot {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("path_thumbnail")
	private String pathThumbnail;
	@JsonProperty("path_full")
	private String pathFull;
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
	 * @return The pathThumbnail
	 */
	@JsonProperty("path_thumbnail")
	public String getPathThumbnail() {
		return pathThumbnail;
	}

	/**
	 *
	 * @param pathThumbnail The path_thumbnail
	 */
	@JsonProperty("path_thumbnail")
	public void setPathThumbnail(String pathThumbnail) {
		this.pathThumbnail=pathThumbnail;
	}

	/**
	 *
	 * @return The pathFull
	 */
	@JsonProperty("path_full")
	public String getPathFull() {
		return pathFull;
	}

	/**
	 *
	 * @param pathFull The path_full
	 */
	@JsonProperty("path_full")
	public void setPathFull(String pathFull) {
		this.pathFull=pathFull;
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
		return new HashCodeBuilder().append(id).append(pathThumbnail).append(pathFull).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Screenshot)==false) {
			return false;
		}
		Screenshot rhs=((Screenshot) other);
		return new EqualsBuilder().append(id, rhs.id).append(pathThumbnail, rhs.pathThumbnail).append(pathFull, rhs.pathFull).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
