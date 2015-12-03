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
	"name",
	"title",
	"description",
	"selection_text",
	"save_text",
	"display_type",
	"is_recurring_subscription",
	"subs"
})
public class PackageGroup {

	@JsonProperty("name")
	private String name;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("selection_text")
	private String selectionText;
	@JsonProperty("save_text")
	private String saveText;
	@JsonProperty("display_type")
	private Integer displayType;
	@JsonProperty("is_recurring_subscription")
	private String isRecurringSubscription;
	@JsonProperty("subs")
	@Valid
	private List<Sub> subs=new ArrayList<Sub>();
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
	 * @return The title
	 */
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	/**
	 *
	 * @param title The title
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title=title;
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

	/**
	 *
	 * @return The selectionText
	 */
	@JsonProperty("selection_text")
	public String getSelectionText() {
		return selectionText;
	}

	/**
	 *
	 * @param selectionText The selection_text
	 */
	@JsonProperty("selection_text")
	public void setSelectionText(String selectionText) {
		this.selectionText=selectionText;
	}

	/**
	 *
	 * @return The saveText
	 */
	@JsonProperty("save_text")
	public String getSaveText() {
		return saveText;
	}

	/**
	 *
	 * @param saveText The save_text
	 */
	@JsonProperty("save_text")
	public void setSaveText(String saveText) {
		this.saveText=saveText;
	}

	/**
	 *
	 * @return The displayType
	 */
	@JsonProperty("display_type")
	public Integer getDisplayType() {
		return displayType;
	}

	/**
	 *
	 * @param displayType The display_type
	 */
	@JsonProperty("display_type")
	public void setDisplayType(Integer displayType) {
		this.displayType=displayType;
	}

	/**
	 *
	 * @return The isRecurringSubscription
	 */
	@JsonProperty("is_recurring_subscription")
	public String getIsRecurringSubscription() {
		return isRecurringSubscription;
	}

	/**
	 *
	 * @param isRecurringSubscription The is_recurring_subscription
	 */
	@JsonProperty("is_recurring_subscription")
	public void setIsRecurringSubscription(String isRecurringSubscription) {
		this.isRecurringSubscription=isRecurringSubscription;
	}

	/**
	 *
	 * @return The subs
	 */
	@JsonProperty("subs")
	public List<Sub> getSubs() {
		return subs;
	}

	/**
	 *
	 * @param subs The subs
	 */
	@JsonProperty("subs")
	public void setSubs(List<Sub> subs) {
		this.subs=subs;
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
		return new HashCodeBuilder().append(name).append(title).append(description).append(selectionText).append(saveText).append(displayType).append(isRecurringSubscription).append(subs).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof PackageGroup)==false) {
			return false;
		}
		PackageGroup rhs=((PackageGroup) other);
		return new EqualsBuilder().append(name, rhs.name).append(title, rhs.title).append(description, rhs.description).append(selectionText, rhs.selectionText).append(saveText, rhs.saveText).append(displayType, rhs.displayType).append(isRecurringSubscription, rhs.isRecurringSubscription).append(subs, rhs.subs).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
