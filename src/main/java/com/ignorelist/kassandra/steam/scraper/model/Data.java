package com.ignorelist.kassandra.steam.scraper.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"type",
	"name",
	"steam_appid",
	"required_age",
	"is_free",
	"controller_support",
	"dlc",
	"detailed_description",
	"about_the_game",
	"supported_languages",
	"header_image",
	"website",
	"pc_requirements",
	"mac_requirements",
	"linux_requirements",
	"legal_notice",
	"developers",
	"publishers",
	"price_overview",
	"packages",
	"package_groups",
	"platforms",
	"categories",
	"genres",
	"screenshots",
	"recommendations",
	"achievements",
	"release_date",
	"support_info",
	"background"
})
public class Data {

	@JsonProperty("type")
	private String type;
	@JsonProperty("name")
	private String name;
	@JsonProperty("steam_appid")
	private Integer steamAppid;
	@JsonProperty("required_age")
	private Integer requiredAge;
	@JsonProperty("is_free")
	private Boolean isFree;
	@JsonProperty("controller_support")
	private String controllerSupport;
	@JsonProperty("dlc")
	@Valid
	private List<Integer> dlc=new ArrayList<Integer>();
	@JsonProperty("detailed_description")
	private String detailedDescription;
	@JsonProperty("about_the_game")
	private String aboutTheGame;
	@JsonProperty("supported_languages")
	private String supportedLanguages;
	@JsonProperty("header_image")
	private String headerImage;
	@JsonProperty("website")
	private String website;
	@JsonProperty("pc_requirements")
	@Valid
	private List<Requirement> pcRequirements;
	@JsonProperty("mac_requirements")
	@Valid
	private List<Requirement> macRequirements;
	@JsonProperty("linux_requirements")
	@Valid
	private List<Requirement> linuxRequirements;
	@JsonProperty("legal_notice")
	private String legalNotice;
	@JsonProperty("developers")
	@Valid
	private List<String> developers=new ArrayList<String>();
	@JsonProperty("publishers")
	@Valid
	private List<String> publishers=new ArrayList<String>();
	@JsonProperty("price_overview")
	@Valid
	private PriceOverview priceOverview;
	@JsonProperty("packages")
	@Valid
	private List<String> packages=new ArrayList<String>();
	@JsonProperty("package_groups")
	@Valid
	private List<PackageGroup> packageGroups=new ArrayList<PackageGroup>();
	@JsonProperty("platforms")
	@Valid
	private Platforms platforms;
	@JsonProperty("categories")
	@Valid
	private List<Category> categories=new ArrayList<Category>();
	@JsonProperty("genres")
	@Valid
	private List<Genre> genres=new ArrayList<Genre>();
	@JsonProperty("screenshots")
	@Valid
	private List<Screenshot> screenshots=new ArrayList<Screenshot>();
	@JsonProperty("recommendations")
	@Valid
	private Recommendations recommendations;
	@JsonProperty("achievements")
	@Valid
	private Achievements achievements;
	@JsonProperty("release_date")
	@Valid
	private ReleaseDate releaseDate;
	@JsonProperty("support_info")
	@Valid
	private SupportInfo supportInfo;
	@JsonProperty("background")
	private String background;
	@JsonIgnore
	private Map<String, Object> additionalProperties=new HashMap<String, Object>();

	/**
	 *
	 * @return The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 *
	 * @param type The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type=type;
	}

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
	 * @return The steamAppid
	 */
	@JsonProperty("steam_appid")
	public Integer getSteamAppid() {
		return steamAppid;
	}

	/**
	 *
	 * @param steamAppid The steam_appid
	 */
	@JsonProperty("steam_appid")
	public void setSteamAppid(Integer steamAppid) {
		this.steamAppid=steamAppid;
	}

	/**
	 *
	 * @return The requiredAge
	 */
	@JsonProperty("required_age")
	public Integer getRequiredAge() {
		return requiredAge;
	}

	/**
	 *
	 * @param requiredAge The required_age
	 */
	@JsonProperty("required_age")
	public void setRequiredAge(Integer requiredAge) {
		this.requiredAge=requiredAge;
	}

	/**
	 *
	 * @return The isFree
	 */
	@JsonProperty("is_free")
	public Boolean getIsFree() {
		return isFree;
	}

	/**
	 *
	 * @param isFree The is_free
	 */
	@JsonProperty("is_free")
	public void setIsFree(Boolean isFree) {
		this.isFree=isFree;
	}

	/**
	 *
	 * @return The controllerSupport
	 */
	@JsonProperty("controller_support")
	public String getControllerSupport() {
		return controllerSupport;
	}

	/**
	 *
	 * @param controllerSupport The controller_support
	 */
	@JsonProperty("controller_support")
	public void setControllerSupport(String controllerSupport) {
		this.controllerSupport=controllerSupport;
	}

	/**
	 *
	 * @return The dlc
	 */
	@JsonProperty("dlc")
	public List<Integer> getDlc() {
		return dlc;
	}

	/**
	 *
	 * @param dlc The dlc
	 */
	@JsonProperty("dlc")
	public void setDlc(List<Integer> dlc) {
		this.dlc=dlc;
	}

	/**
	 *
	 * @return The detailedDescription
	 */
	@JsonProperty("detailed_description")
	public String getDetailedDescription() {
		return detailedDescription;
	}

	/**
	 *
	 * @param detailedDescription The detailed_description
	 */
	@JsonProperty("detailed_description")
	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription=detailedDescription;
	}

	/**
	 *
	 * @return The aboutTheGame
	 */
	@JsonProperty("about_the_game")
	public String getAboutTheGame() {
		return aboutTheGame;
	}

	/**
	 *
	 * @param aboutTheGame The about_the_game
	 */
	@JsonProperty("about_the_game")
	public void setAboutTheGame(String aboutTheGame) {
		this.aboutTheGame=aboutTheGame;
	}

	/**
	 *
	 * @return The supportedLanguages
	 */
	@JsonProperty("supported_languages")
	public String getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 *
	 * @param supportedLanguages The supported_languages
	 */
	@JsonProperty("supported_languages")
	public void setSupportedLanguages(String supportedLanguages) {
		this.supportedLanguages=supportedLanguages;
	}

	/**
	 *
	 * @return The headerImage
	 */
	@JsonProperty("header_image")
	public String getHeaderImage() {
		return headerImage;
	}

	/**
	 *
	 * @param headerImage The header_image
	 */
	@JsonProperty("header_image")
	public void setHeaderImage(String headerImage) {
		this.headerImage=headerImage;
	}

	/**
	 *
	 * @return The website
	 */
	@JsonProperty("website")
	public String getWebsite() {
		return website;
	}

	/**
	 *
	 * @param website The website
	 */
	@JsonProperty("website")
	public void setWebsite(String website) {
		this.website=website;
	}

	/**
	 *
	 * @return The pcRequirements
	 */
	@JsonProperty("pc_requirements")
	public List<Requirement> getPcRequirements() {
		return pcRequirements;
	}

	/**
	 *
	 * @param pcRequirements The pc_requirements
	 */
	@JsonProperty("pc_requirements")
	public void setPcRequirements(List<Requirement> pcRequirements) {
		this.pcRequirements=pcRequirements;
	}

	/**
	 *
	 * @return The macRequirements
	 */
	@JsonProperty("mac_requirements")
	public List<Requirement> getMacRequirements() {
		return macRequirements;
	}

	/**
	 *
	 * @param macRequirements The mac_requirements
	 */
	@JsonProperty("mac_requirements")
	public void setMacRequirements(List<Requirement> macRequirements) {
		this.macRequirements=macRequirements;
	}

	/**
	 *
	 * @return The linuxRequirements
	 */
	@JsonProperty("linux_requirements")
	public List<Requirement> getLinuxRequirements() {
		return linuxRequirements;
	}

	/**
	 *
	 * @param linuxRequirements The linux_requirements
	 */
	@JsonProperty("linux_requirements")
	public void setLinuxRequirements(List<Requirement> linuxRequirements) {
		this.linuxRequirements=linuxRequirements;
	}

	/**
	 *
	 * @return The legalNotice
	 */
	@JsonProperty("legal_notice")
	public String getLegalNotice() {
		return legalNotice;
	}

	/**
	 *
	 * @param legalNotice The legal_notice
	 */
	@JsonProperty("legal_notice")
	public void setLegalNotice(String legalNotice) {
		this.legalNotice=legalNotice;
	}

	/**
	 *
	 * @return The developers
	 */
	@JsonProperty("developers")
	public List<String> getDevelopers() {
		return developers;
	}

	/**
	 *
	 * @param developers The developers
	 */
	@JsonProperty("developers")
	public void setDevelopers(List<String> developers) {
		this.developers=developers;
	}

	/**
	 *
	 * @return The publishers
	 */
	@JsonProperty("publishers")
	public List<String> getPublishers() {
		return publishers;
	}

	/**
	 *
	 * @param publishers The publishers
	 */
	@JsonProperty("publishers")
	public void setPublishers(List<String> publishers) {
		this.publishers=publishers;
	}

	/**
	 *
	 * @return The priceOverview
	 */
	@JsonProperty("price_overview")
	public PriceOverview getPriceOverview() {
		return priceOverview;
	}

	/**
	 *
	 * @param priceOverview The price_overview
	 */
	@JsonProperty("price_overview")
	public void setPriceOverview(PriceOverview priceOverview) {
		this.priceOverview=priceOverview;
	}

	/**
	 *
	 * @return The packages
	 */
	@JsonProperty("packages")
	public List<String> getPackages() {
		return packages;
	}

	/**
	 *
	 * @param packages The packages
	 */
	@JsonProperty("packages")
	public void setPackages(List<String> packages) {
		this.packages=packages;
	}

	/**
	 *
	 * @return The packageGroups
	 */
	@JsonProperty("package_groups")
	public List<PackageGroup> getPackageGroups() {
		return packageGroups;
	}

	/**
	 *
	 * @param packageGroups The package_groups
	 */
	@JsonProperty("package_groups")
	public void setPackageGroups(List<PackageGroup> packageGroups) {
		this.packageGroups=packageGroups;
	}

	/**
	 *
	 * @return The platforms
	 */
	@JsonProperty("platforms")
	public Platforms getPlatforms() {
		return platforms;
	}

	/**
	 *
	 * @param platforms The platforms
	 */
	@JsonProperty("platforms")
	public void setPlatforms(Platforms platforms) {
		this.platforms=platforms;
	}

	/**
	 *
	 * @return The categories
	 */
	@JsonProperty("categories")
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 *
	 * @param categories The categories
	 */
	@JsonProperty("categories")
	public void setCategories(List<Category> categories) {
		this.categories=categories;
	}

	/**
	 *
	 * @return The genres
	 */
	@JsonProperty("genres")
	public List<Genre> getGenres() {
		return genres;
	}

	/**
	 *
	 * @param genres The genres
	 */
	@JsonProperty("genres")
	public void setGenres(List<Genre> genres) {
		this.genres=genres;
	}

	/**
	 *
	 * @return The screenshots
	 */
	@JsonProperty("screenshots")
	public List<Screenshot> getScreenshots() {
		return screenshots;
	}

	/**
	 *
	 * @param screenshots The screenshots
	 */
	@JsonProperty("screenshots")
	public void setScreenshots(List<Screenshot> screenshots) {
		this.screenshots=screenshots;
	}

	/**
	 *
	 * @return The recommendations
	 */
	@JsonProperty("recommendations")
	public Recommendations getRecommendations() {
		return recommendations;
	}

	/**
	 *
	 * @param recommendations The recommendations
	 */
	@JsonProperty("recommendations")
	public void setRecommendations(Recommendations recommendations) {
		this.recommendations=recommendations;
	}

	/**
	 *
	 * @return The achievements
	 */
	@JsonProperty("achievements")
	public Achievements getAchievements() {
		return achievements;
	}

	/**
	 *
	 * @param achievements The achievements
	 */
	@JsonProperty("achievements")
	public void setAchievements(Achievements achievements) {
		this.achievements=achievements;
	}

	/**
	 *
	 * @return The releaseDate
	 */
	@JsonProperty("release_date")
	public ReleaseDate getReleaseDate() {
		return releaseDate;
	}

	/**
	 *
	 * @param releaseDate The release_date
	 */
	@JsonProperty("release_date")
	public void setReleaseDate(ReleaseDate releaseDate) {
		this.releaseDate=releaseDate;
	}

	/**
	 *
	 * @return The supportInfo
	 */
	@JsonProperty("support_info")
	public SupportInfo getSupportInfo() {
		return supportInfo;
	}

	/**
	 *
	 * @param supportInfo The support_info
	 */
	@JsonProperty("support_info")
	public void setSupportInfo(SupportInfo supportInfo) {
		this.supportInfo=supportInfo;
	}

	/**
	 *
	 * @return The background
	 */
	@JsonProperty("background")
	public String getBackground() {
		return background;
	}

	/**
	 *
	 * @param background The background
	 */
	@JsonProperty("background")
	public void setBackground(String background) {
		this.background=background;
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
		return new HashCodeBuilder().append(type).append(name).append(steamAppid).append(requiredAge).append(isFree).append(controllerSupport).append(dlc).append(detailedDescription).append(aboutTheGame).append(supportedLanguages).append(headerImage).append(website).append(pcRequirements).append(macRequirements).append(linuxRequirements).append(legalNotice).append(developers).append(publishers).append(priceOverview).append(packages).append(packageGroups).append(platforms).append(categories).append(genres).append(screenshots).append(recommendations).append(achievements).append(releaseDate).append(supportInfo).append(background).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other==this) {
			return true;
		}
		if ((other instanceof Data)==false) {
			return false;
		}
		Data rhs=((Data) other);
		return new EqualsBuilder().append(type, rhs.type).append(name, rhs.name).append(steamAppid, rhs.steamAppid).append(requiredAge, rhs.requiredAge).append(isFree, rhs.isFree).append(controllerSupport, rhs.controllerSupport).append(dlc, rhs.dlc).append(detailedDescription, rhs.detailedDescription).append(aboutTheGame, rhs.aboutTheGame).append(supportedLanguages, rhs.supportedLanguages).append(headerImage, rhs.headerImage).append(website, rhs.website).append(pcRequirements, rhs.pcRequirements).append(macRequirements, rhs.macRequirements).append(linuxRequirements, rhs.linuxRequirements).append(legalNotice, rhs.legalNotice).append(developers, rhs.developers).append(publishers, rhs.publishers).append(priceOverview, rhs.priceOverview).append(packages, rhs.packages).append(packageGroups, rhs.packageGroups).append(platforms, rhs.platforms).append(categories, rhs.categories).append(genres, rhs.genres).append(screenshots, rhs.screenshots).append(recommendations, rhs.recommendations).append(achievements, rhs.achievements).append(releaseDate, rhs.releaseDate).append(supportInfo, rhs.supportInfo).append(background, rhs.background).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
