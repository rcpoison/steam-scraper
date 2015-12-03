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
	"packageid",
	"percent_savings_text",
	"percent_savings",
	"option_text",
	"option_description",
	"can_get_free_license",
	"is_free_license",
	"price_in_cents_with_discount"
})
public class Sub {

	@JsonProperty("packageid")
	private String packageid;
	@JsonProperty("percent_savings_text")
	private String percentSavingsText;
	@JsonProperty("percent_savings")
	private Integer percentSavings;
	@JsonProperty("option_text")
	private String optionText;
	@JsonProperty("option_description")
	private String optionDescription;
	@JsonProperty("can_get_free_license")
	private String canGetFreeLicense;
	@JsonProperty("is_free_license")
	private Boolean isFreeLicense;
	@JsonProperty("price_in_cents_with_discount")
	private Integer priceInCentsWithDiscount;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The packageid
	 */
	@JsonProperty("packageid")
	public String getPackageid() {
		return packageid;
	}

	/**
	 *
	 * @param packageid The packageid
	 */
	@JsonProperty("packageid")
	public void setPackageid(String packageid) {
		this.packageid=packageid;
	}

	/**
	 *
	 * @return The percentSavingsText
	 */
	@JsonProperty("percent_savings_text")
	public String getPercentSavingsText() {
		return percentSavingsText;
	}

	/**
	 *
	 * @param percentSavingsText The percent_savings_text
	 */
	@JsonProperty("percent_savings_text")
	public void setPercentSavingsText(String percentSavingsText) {
		this.percentSavingsText=percentSavingsText;
	}

	/**
	 *
	 * @return The percentSavings
	 */
	@JsonProperty("percent_savings")
	public Integer getPercentSavings() {
		return percentSavings;
	}

	/**
	 *
	 * @param percentSavings The percent_savings
	 */
	@JsonProperty("percent_savings")
	public void setPercentSavings(Integer percentSavings) {
		this.percentSavings=percentSavings;
	}

	/**
	 *
	 * @return The optionText
	 */
	@JsonProperty("option_text")
	public String getOptionText() {
		return optionText;
	}

	/**
	 *
	 * @param optionText The option_text
	 */
	@JsonProperty("option_text")
	public void setOptionText(String optionText) {
		this.optionText=optionText;
	}

	/**
	 *
	 * @return The optionDescription
	 */
	@JsonProperty("option_description")
	public String getOptionDescription() {
		return optionDescription;
	}

	/**
	 *
	 * @param optionDescription The option_description
	 */
	@JsonProperty("option_description")
	public void setOptionDescription(String optionDescription) {
		this.optionDescription=optionDescription;
	}

	/**
	 *
	 * @return The canGetFreeLicense
	 */
	@JsonProperty("can_get_free_license")
	public String getCanGetFreeLicense() {
		return canGetFreeLicense;
	}

	/**
	 *
	 * @param canGetFreeLicense The can_get_free_license
	 */
	@JsonProperty("can_get_free_license")
	public void setCanGetFreeLicense(String canGetFreeLicense) {
		this.canGetFreeLicense=canGetFreeLicense;
	}

	/**
	 *
	 * @return The isFreeLicense
	 */
	@JsonProperty("is_free_license")
	public Boolean getIsFreeLicense() {
		return isFreeLicense;
	}

	/**
	 *
	 * @param isFreeLicense The is_free_license
	 */
	@JsonProperty("is_free_license")
	public void setIsFreeLicense(Boolean isFreeLicense) {
		this.isFreeLicense=isFreeLicense;
	}

	/**
	 *
	 * @return The priceInCentsWithDiscount
	 */
	@JsonProperty("price_in_cents_with_discount")
	public Integer getPriceInCentsWithDiscount() {
		return priceInCentsWithDiscount;
	}

	/**
	 *
	 * @param priceInCentsWithDiscount The price_in_cents_with_discount
	 */
	@JsonProperty("price_in_cents_with_discount")
	public void setPriceInCentsWithDiscount(Integer priceInCentsWithDiscount) {
		this.priceInCentsWithDiscount=priceInCentsWithDiscount;
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
		return new HashCodeBuilder().append(packageid).append(percentSavingsText).append(percentSavings).append(optionText).append(optionDescription).append(canGetFreeLicense).append(isFreeLicense).append(priceInCentsWithDiscount).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Sub)==false) {
			return false;
		}
		Sub rhs=((Sub) other);
		return new EqualsBuilder().append(packageid, rhs.packageid).append(percentSavingsText, rhs.percentSavingsText).append(percentSavings, rhs.percentSavings).append(optionText, rhs.optionText).append(optionDescription, rhs.optionDescription).append(canGetFreeLicense, rhs.canGetFreeLicense).append(isFreeLicense, rhs.isFreeLicense).append(priceInCentsWithDiscount, rhs.priceInCentsWithDiscount).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
