package com.ignorelist.kassandra.steam.scraper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
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
	"total",
	"highlighted"
})
public class Achievements {

	@JsonProperty("total")
	private Integer total;
	@JsonProperty("highlighted")
	@Valid
	private List<Highlighted> highlighted=new ArrayList<Highlighted>();
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The total
	 */
	@JsonProperty("total")
	public Integer getTotal() {
		return total;
	}

	/**
	 *
	 * @param total The total
	 */
	@JsonProperty("total")
	public void setTotal(Integer total) {
		this.total=total;
	}

	/**
	 *
	 * @return The highlighted
	 */
	@JsonProperty("highlighted")
	public List<Highlighted> getHighlighted() {
		return highlighted;
	}

	/**
	 *
	 * @param highlighted The highlighted
	 */
	@JsonProperty("highlighted")
	public void setHighlighted(List<Highlighted> highlighted) {
		this.highlighted=highlighted;
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
		return new HashCodeBuilder().append(total).append(highlighted).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Achievements)==false) {
			return false;
		}
		Achievements rhs=((Achievements) other);
		return new EqualsBuilder().append(total, rhs.total).append(highlighted, rhs.highlighted).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
