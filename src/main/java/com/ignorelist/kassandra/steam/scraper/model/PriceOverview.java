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
	"currency",
	"initial",
	"final",
	"discount_percent"
})
public class PriceOverview {

	@JsonProperty("currency")
	private String currency;
	@JsonProperty("initial")
	private Integer initial;
	@JsonProperty("final")
	private Integer _final;
	@JsonProperty("discount_percent")
	private Integer discountPercent;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The currency
	 */
	@JsonProperty("currency")
	public String getCurrency() {
		return currency;
	}

	/**
	 *
	 * @param currency The currency
	 */
	@JsonProperty("currency")
	public void setCurrency(String currency) {
		this.currency=currency;
	}

	/**
	 *
	 * @return The initial
	 */
	@JsonProperty("initial")
	public Integer getInitial() {
		return initial;
	}

	/**
	 *
	 * @param initial The initial
	 */
	@JsonProperty("initial")
	public void setInitial(Integer initial) {
		this.initial=initial;
	}

	/**
	 *
	 * @return The _final
	 */
	@JsonProperty("final")
	public Integer getFinal() {
		return _final;
	}

	/**
	 *
	 * @param _final The final
	 */
	@JsonProperty("final")
	public void setFinal(Integer _final) {
		this._final=_final;
	}

	/**
	 *
	 * @return The discountPercent
	 */
	@JsonProperty("discount_percent")
	public Integer getDiscountPercent() {
		return discountPercent;
	}

	/**
	 *
	 * @param discountPercent The discount_percent
	 */
	@JsonProperty("discount_percent")
	public void setDiscountPercent(Integer discountPercent) {
		this.discountPercent=discountPercent;
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
		return new HashCodeBuilder().append(currency).append(initial).append(_final).append(discountPercent).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof PriceOverview)==false) {
			return false;
		}
		PriceOverview rhs=((PriceOverview) other);
		return new EqualsBuilder().append(currency, rhs.currency).append(initial, rhs.initial).append(_final, rhs._final).append(discountPercent, rhs.discountPercent).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
