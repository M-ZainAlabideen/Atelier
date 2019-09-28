package app.atelier.webservices.responses.products;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import app.atelier.webservices.responses.attributes.AttributeModel;
import app.atelier.webservices.responses.vendors.VendorModel;

public class ProductModel implements Serializable {
    @SerializedName("show_timer")
    @Expose
    public Boolean showTimer;
    @SerializedName("visible_individually")
    @Expose
    public Boolean visibleIndividually;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("localized_names")
    @Expose
    public List<LocalizedNameModel> localizedNames = null;
    @SerializedName("short_description")
    @Expose
    public String shortDescription;
    @SerializedName("full_description")
    @Expose
    public String fullDescription;
    @SerializedName("show_on_home_page")
    @Expose
    public Boolean showOnHomePage;
    @SerializedName("allow_customer_reviews")
    @Expose
    public Boolean allowCustomerReviews;
    @SerializedName("approved_rating_sum")
    @Expose
    public Integer approvedRatingSum;
    @SerializedName("not_approved_rating_sum")
    @Expose
    public Integer notApprovedRatingSum;
    @SerializedName("approved_total_reviews")
    @Expose
    public Integer approvedTotalReviews;
    @SerializedName("not_approved_total_reviews")
    @Expose
    public Integer notApprovedTotalReviews;
    @SerializedName("require_other_products")
    @Expose
    public Boolean requireOtherProducts;
    @SerializedName("automatically_add_required_products")
    @Expose
    public Boolean automaticallyAddRequiredProducts;
    @SerializedName("is_download")
    @Expose
    public Boolean isDownload;
    @SerializedName("unlimited_downloads")
    @Expose
    public Boolean unlimitedDownloads;
    @SerializedName("max_number_of_downloads")
    @Expose
    public Integer maxNumberOfDownloads;
    @SerializedName("has_sample_download")
    @Expose
    public Boolean hasSampleDownload;
    @SerializedName("has_user_agreement")
    @Expose
    public Boolean hasUserAgreement;
    @SerializedName("is_recurring")
    @Expose
    public Boolean isRecurring;
    @SerializedName("recurring_cycle_length")
    @Expose
    public Integer recurringCycleLength;
    @SerializedName("recurring_total_cycles")
    @Expose
    public Integer recurringTotalCycles;
    @SerializedName("is_rental")
    @Expose
    public Boolean isRental;
    @SerializedName("rental_price_length")
    @Expose
    public Integer rentalPriceLength;
    @SerializedName("is_ship_enabled")
    @Expose
    public Boolean isShipEnabled;
    @SerializedName("is_free_shipping")
    @Expose
    public Boolean isFreeShipping;
    @SerializedName("ship_separately")
    @Expose
    public Boolean shipSeparately;
    @SerializedName("additional_shipping_charge")
    @Expose
    public Integer additionalShippingCharge;
    @SerializedName("is_tax_exempt")
    @Expose
    public Boolean isTaxExempt;
    @SerializedName("is_telecommunications_or_broadcasting_or_electronic_services")
    @Expose
    public Boolean isTelecommunicationsOrBroadcastingOrElectronicServices;
    @SerializedName("use_multiple_warehouses")
    @Expose
    public Boolean useMultipleWarehouses;
    @SerializedName("manage_inventory_method_id")
    @Expose
    public Integer manageInventoryMethodId;
    @SerializedName("stock_quantity")
    @Expose
    public Integer stockQuantity;
    @SerializedName("display_stock_availability")
    @Expose
    public Boolean displayStockAvailability;
    @SerializedName("display_stock_quantity")
    @Expose
    public Boolean displayStockQuantity;
    @SerializedName("min_stock_quantity")
    @Expose
    public Integer minStockQuantity;
    @SerializedName("notify_admin_for_quantity_below")
    @Expose
    public Integer notifyAdminForQuantityBelow;
    @SerializedName("allow_back_in_stock_subscriptions")
    @Expose
    public Boolean allowBackInStockSubscriptions;
    @SerializedName("order_minimum_quantity")
    @Expose
    public Integer orderMinimumQuantity;
    @SerializedName("order_maximum_quantity")
    @Expose
    public Integer orderMaximumQuantity;
    @SerializedName("allow_adding_only_existing_attribute_combinations")
    @Expose
    public Boolean allowAddingOnlyExistingAttributeCombinations;
    @SerializedName("disable_buy_button")
    @Expose
    public Boolean disableBuyButton;
    @SerializedName("disable_wishlist_button")
    @Expose
    public Boolean disableWishlistButton;
    @SerializedName("available_for_pre_order")
    @Expose
    public Boolean availableForPreOrder;
    @SerializedName("call_for_price")
    @Expose
    public Boolean callForPrice;
    @SerializedName("price")
    @Expose
    public Double price;
    @SerializedName("old_price")
    @Expose
    public Double oldPrice;
    @SerializedName("product_cost")
    @Expose
    public Integer productCost;
    @SerializedName("customer_enters_price")
    @Expose
    public Boolean customerEntersPrice;
    @SerializedName("minimum_customer_entered_price")
    @Expose
    public Double minimumCustomerEnteredPrice;
    @SerializedName("maximum_customer_entered_price")
    @Expose
    public Double maximumCustomerEnteredPrice;
    @SerializedName("baseprice_enabled")
    @Expose
    public Boolean basepriceEnabled;
    @SerializedName("baseprice_amount")
    @Expose
    public Double basepriceAmount;
    @SerializedName("baseprice_base_amount")
    @Expose
    public Double basepriceBaseAmount;
    @SerializedName("has_tier_prices")
    @Expose
    public Boolean hasTierPrices;
    @SerializedName("has_discounts_applied")
    @Expose
    public Boolean hasDiscountsApplied;
    @SerializedName("weight")
    @Expose
    public Integer weight;
    @SerializedName("length")
    @Expose
    public Integer length;
    @SerializedName("width")
    @Expose
    public Integer width;
    @SerializedName("height")
    @Expose
    public Integer height;
    @SerializedName("available_start_date_time_utc")
    @Expose
    public String availableStartDateTimeUtc;
    @SerializedName("available_end_date_time_utc")
    @Expose
    public String availableEndDateTimeUtc;
    @SerializedName("display_order")
    @Expose
    public Integer displayOrder;
    @SerializedName("published")
    @Expose
    public Boolean published;
    @SerializedName("deleted")
    @Expose
    public Boolean deleted;
    @SerializedName("created_on_utc")
    @Expose
    public String createdOnUtc;
    @SerializedName("updated_on_utc")
    @Expose
    public String updatedOnUtc;
    @SerializedName("product_type")
    @Expose
    public String productType;
    @SerializedName("parent_grouped_product_id")
    @Expose
    public Integer parentGroupedProductId;
    @SerializedName("role_ids")
    @Expose
    public List<Object> roleIds = null;
    @SerializedName("discount_ids")
    @Expose
    public List<Object> discountIds = null;
    @SerializedName("store_ids")
    @Expose
    public List<Object> storeIds = null;
    @SerializedName("manufacturer_ids")
    @Expose
    public List<Object> manufacturerIds = null;
    @SerializedName("images")
    @Expose
    public ArrayList<ImageModel> images = null;


    @SerializedName("attributes")
    @Expose
    public List<AttributeModel> attributes = null;

    @SerializedName("Is_AddedToWishList")
    @Expose
    public boolean IsAddedToWishList;

    @SerializedName("product_attribute_combinations")
    @Expose
    public List<Object> productAttributeCombinations = null;
    @SerializedName("product_specification_attributes")
    @Expose
    public List<Object> productSpecificationAttributes = null;
    @SerializedName("associated_product_ids")
    @Expose
    public List<Object> associatedProductIds = null;
    @SerializedName("tags")
    @Expose
    public ArrayList<String> tags = null;
    @SerializedName("vendor_id")
    @Expose
    public Integer vendorId;
    @SerializedName("se_name")
    @Expose
    public String seName;
    @SerializedName("id")
    @Expose
    public Integer id;

    Integer quantity;

    @SerializedName("is_card")
    @Expose
    public Boolean isCard;
    @SerializedName("sold_quantity")
    @Expose
    public Integer soldQuantity;
    @SerializedName("stock_availability")
    @Expose
    public String stockAvailability;
    @SerializedName("formatted_price")
    @Expose
    public String formattedPrice;
    @SerializedName("formatted_old_price")
    @Expose
    public String formattedOldPrice;
    @SerializedName("prices_percentage")
    @Expose
    public Double pricesPercentage;
    @SerializedName("vendor")
    @Expose
    public VendorModel vendor;

    @SerializedName("localized_short_descriptions")
    @Expose
    public List<ShortDescriptionModel> localizedShortDescriptions;

    @SerializedName("localized_full_descriptions")
    @Expose
    public List<FullDescriptionModel> FullDescriptionModels;

    public String getPhoto(){

        String url="";

        if(images != null){

            if(images.size()>0){

                if(images.get(0).src != null){

                    url = images.get(0).src;

                }

            }

        }

        return url;

    }

    public Double getDiscountPercentage(){

        return (100- ((price/oldPrice) * 100));

    }

    public String getLocalizedName(){

        if(localizedNames != null){

            if(localizedNames.size()>0){

                name = localizedNames.get(0).localizedName;

            }

        }

        return name;

    }

    public String getShortDescription(){


        if(localizedShortDescriptions != null){

            if(localizedShortDescriptions.size()>0){

                shortDescription = localizedShortDescriptions.get(0).shortDescription;

            }

        }

        return name;

    }

    public String getFullDescription(){


        if(FullDescriptionModels != null){

            if(FullDescriptionModels.size()>0){

                fullDescription = FullDescriptionModels.get(0).fullDescription;

            }

        }

        return name;

    }

}
