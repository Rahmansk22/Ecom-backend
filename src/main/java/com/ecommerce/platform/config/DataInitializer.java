package com.ecommerce.platform.config;

import com.ecommerce.platform.model.Category;
import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.model.ProductImage;
import com.ecommerce.platform.model.ProductVariant;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.repository.ProductImageRepository;
import com.ecommerce.platform.repository.ProductRepository;
import com.ecommerce.platform.repository.ProductVariantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final com.ecommerce.platform.repository.OrderRepository orderRepository;
    private final com.ecommerce.platform.repository.ReviewRepository reviewRepository;
    private final com.ecommerce.platform.repository.WishlistRepository wishlistRepository;
    private final com.ecommerce.platform.repository.UserRepository userRepository;
    private final org.springframework.cache.CacheManager cacheManager;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository,
                           ProductVariantRepository variantRepository, ProductImageRepository imageRepository,
                           com.ecommerce.platform.repository.OrderRepository orderRepository,
                           com.ecommerce.platform.repository.ReviewRepository reviewRepository,
                           com.ecommerce.platform.repository.WishlistRepository wishlistRepository,
                           com.ecommerce.platform.repository.UserRepository userRepository,
                           org.springframework.cache.CacheManager cacheManager) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... args) throws Exception {
        // Evict cache to purge any old/deleted database IDs
        if (cacheManager != null && cacheManager.getCache("products") != null) {
            cacheManager.getCache("products").clear();
        }

        // Delete references first to prevent foreign key constraint violations
        reviewRepository.deleteAll();
        wishlistRepository.deleteAll();
        orderRepository.deleteAll();

        // ALWAYS clear repositories to start with a clean state and avoid duplicate key / unique constraint errors
        imageRepository.deleteAll();
        variantRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // 1. Seed Categories
        Map<String, Category> catMap = new HashMap<>();

        Category mobiles = categoryRepository.save(Category.builder()
                .name("Mobiles")
                .slug("mobiles")
                .bannerUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("mobiles", mobiles);

        Category electronics = categoryRepository.save(Category.builder()
                .name("Electronics")
                .slug("electronics")
                .bannerUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("electronics", electronics);

        Category tvs_appliances = categoryRepository.save(Category.builder()
                .name("TVs & Appliances")
                .slug("tvs-appliances")
                .bannerUrl("https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("tvs-appliances", tvs_appliances);

        Category men = categoryRepository.save(Category.builder()
                .name("Men's Fashion")
                .slug("men")
                .bannerUrl("https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("men", men);

        Category women = categoryRepository.save(Category.builder()
                .name("Women's Fashion")
                .slug("women")
                .bannerUrl("https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1483985988355-763728e1935b?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("women", women);

        Category baby_kids = categoryRepository.save(Category.builder()
                .name("Baby & Kids")
                .slug("baby-kids")
                .bannerUrl("https://images.unsplash.com/photo-1515488042361-404e9250afef?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1515488042361-404e9250afef?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("baby-kids", baby_kids);

        Category home_furniture = categoryRepository.save(Category.builder()
                .name("Home & Furniture")
                .slug("home-furniture")
                .bannerUrl("https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("home-furniture", home_furniture);

        Category sports_books = categoryRepository.save(Category.builder()
                .name("Sports & Books")
                .slug("sports-books")
                .bannerUrl("https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("sports-books", sports_books);

        Category grocery = categoryRepository.save(Category.builder()
                .name("Grocery")
                .slug("grocery")
                .bannerUrl("https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("grocery", grocery);

        Category travel = categoryRepository.save(Category.builder()
                .name("Flights & Hotels")
                .slug("travel")
                .bannerUrl("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1200&auto=format&fit=crop&q=80")
                .iconUrl("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=100&auto=format&fit=crop&q=80")
                .build());
        catMap.put("travel", travel);

        // Seeding Category: mobiles

        Product prod_1 = productRepository.save(Product.builder()
                .title("Apple iPhone 15 Pro")
                .slug("apple-iphone-15-pro")
                .brand("Apple")
                .category(catMap.get("mobiles"))
                .description("Aerospace-grade titanium design. Powered by A17 Pro chip for next-level graphics and mobile gaming.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_1_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_1)
                .sku("IPH15P-BK-128")
                .price(new BigDecimal("109900.0"))
                .compareAtPrice(new BigDecimal("129900.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Black Titanium\",\"storage\":\"128GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_1_var_1)
                .imageUrl("https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        ProductVariant prod_1_var_2 = variantRepository.save(ProductVariant.builder()
                .product(prod_1)
                .sku("IPH15P-BL-256")
                .price(new BigDecimal("119900.0"))
                .compareAtPrice(new BigDecimal("139900.0"))
                .stock(8)
                .attributesJson("{\"color\":\"Blue Titanium\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_1_var_2)
                .imageUrl("https://images.unsplash.com/photo-1695048133100-33758b29df92?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_2 = productRepository.save(Product.builder()
                .title("Samsung Galaxy S24 Ultra")
                .slug("samsung-galaxy-s24-ultra")
                .brand("Samsung")
                .category(catMap.get("mobiles"))
                .description("Built with titanium armor, featuring the Snapdragon 8 Gen 3 and advanced Galaxy AI capabilities.")
                .status("ACTIVE")
                .countryOfOrigin("South Korea")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_2_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_2)
                .sku("S24U-GY-256")
                .price(new BigDecimal("129900.0"))
                .compareAtPrice(new BigDecimal("139900.0"))
                .stock(20)
                .attributesJson("{\"color\":\"Titanium Gray\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_2_var_1)
                .imageUrl("https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        ProductVariant prod_2_var_2 = variantRepository.save(ProductVariant.builder()
                .product(prod_2)
                .sku("S24U-YL-512")
                .price(new BigDecimal("139900.0"))
                .compareAtPrice(new BigDecimal("154900.0"))
                .stock(10)
                .attributesJson("{\"color\":\"Titanium Yellow\",\"storage\":\"512GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_2_var_2)
                .imageUrl("https://images.unsplash.com/photo-1583573636246-18cb2246697f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_3 = productRepository.save(Product.builder()
                .title("Google Pixel 8 Pro")
                .slug("google-pixel-8-pro")
                .brand("Google")
                .category(catMap.get("mobiles"))
                .description("The all-pro phone engineered by Google. It has the best of Google AI and the most advanced Pixel Camera yet.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_3_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_3)
                .sku("PIX8P-BL-128")
                .price(new BigDecimal("99900.0"))
                .compareAtPrice(new BigDecimal("109900.0"))
                .stock(12)
                .attributesJson("{\"color\":\"Bay Blue\",\"storage\":\"128GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_3_var_1)
                .imageUrl("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_4 = productRepository.save(Product.builder()
                .title("OnePlus 12")
                .slug("oneplus-12")
                .brand("OnePlus")
                .category(catMap.get("mobiles"))
                .description("Redefined flagship. Featuring Snapdragon 8 Gen 3, 16GB RAM, and 4th Gen Hasselblad Camera system.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_4_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_4)
                .sku("OP12-GR-512")
                .price(new BigDecimal("69999.0"))
                .compareAtPrice(new BigDecimal("74999.0"))
                .stock(25)
                .attributesJson("{\"color\":\"Flowy Emerald\",\"storage\":\"512GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_4_var_1)
                .imageUrl("https://images.unsplash.com/photo-1565630916779-e303be97b6f5?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_5 = productRepository.save(Product.builder()
                .title("Xiaomi 14 Ultra")
                .slug("xiaomi-14-ultra")
                .brand("Xiaomi")
                .category(catMap.get("mobiles"))
                .description("Co-engineered with Leica. Quad camera system with large 1-inch sensor and variable aperture.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_5_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_5)
                .sku("X14U-BK-512")
                .price(new BigDecimal("99999.0"))
                .compareAtPrice(new BigDecimal("119999.0"))
                .stock(5)
                .attributesJson("{\"color\":\"Black\",\"storage\":\"512GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_5_var_1)
                .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_6 = productRepository.save(Product.builder()
                .title("Apple iPhone 14")
                .slug("apple-iphone-14")
                .brand("Apple")
                .category(catMap.get("mobiles"))
                .description("Featuring a 6.1-inch Super Retina XDR display, advanced dual-camera system, and Crash Detection.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_6_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_6)
                .sku("IPH14-BL-128")
                .price(new BigDecimal("59900.0"))
                .compareAtPrice(new BigDecimal("69900.0"))
                .stock(18)
                .attributesJson("{\"color\":\"Blue\",\"storage\":\"128GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_6_var_1)
                .imageUrl("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_7 = productRepository.save(Product.builder()
                .title("Samsung Galaxy Z Fold 5")
                .slug("samsung-galaxy-z-fold-5")
                .brand("Samsung")
                .category(catMap.get("mobiles"))
                .description("The ultimate 7.6-inch main screen. Fold it up, slip it in your pocket, and enjoy a massive immersive display.")
                .status("ACTIVE")
                .countryOfOrigin("South Korea")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_7_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_7)
                .sku("ZFOLD5-BK-512")
                .price(new BigDecimal("154900.0"))
                .compareAtPrice(new BigDecimal("164900.0"))
                .stock(7)
                .attributesJson("{\"color\":\"Phantom Black\",\"storage\":\"512GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_7_var_1)
                .imageUrl("https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_8 = productRepository.save(Product.builder()
                .title("Nothing Phone (2)")
                .slug("nothing-phone-2")
                .brand("Nothing")
                .category(catMap.get("mobiles"))
                .description("A new way to interact. Unique Glyph Interface, Nothing OS 2.0, and premium dual rear cameras.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_8_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_8)
                .sku("NOTH2-WH-256")
                .price(new BigDecimal("44999.0"))
                .compareAtPrice(new BigDecimal("49999.0"))
                .stock(30)
                .attributesJson("{\"color\":\"White\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_8_var_1)
                .imageUrl("https://images.unsplash.com/photo-1621330396173-e41b1cafd17f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_9 = productRepository.save(Product.builder()
                .title("Motorola Edge 50 Pro")
                .slug("motorola-edge-50-pro")
                .brand("Motorola")
                .category(catMap.get("mobiles"))
                .description("AI-powered camera, 144Hz curved pOLED display, and 125W TurboPower charging speed.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_9_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_9)
                .sku("MOTO50-PR-256")
                .price(new BigDecimal("31999.0"))
                .compareAtPrice(new BigDecimal("35999.0"))
                .stock(22)
                .attributesJson("{\"color\":\"Luxe Lavender\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_9_var_1)
                .imageUrl("https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_10 = productRepository.save(Product.builder()
                .title("Realme GT 5")
                .slug("realme-gt-5")
                .brand("Realme")
                .category(catMap.get("mobiles"))
                .description("Speed flagship. Equipped with Snapdragon 8 Gen 2, 240W fast charging, and futuristic design.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_10_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_10)
                .sku("RGT5-SL-256")
                .price(new BigDecimal("37999.0"))
                .compareAtPrice(new BigDecimal("41999.0"))
                .stock(14)
                .attributesJson("{\"color\":\"Silver\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_10_var_1)
                .imageUrl("https://images.unsplash.com/photo-1558885561-56c2a0480561?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_oppo = productRepository.save(Product.builder()
                .title("OPPO K14x 5G (Icy Blue, 64 GB) (4 GB RAM)")
                .slug("oppo-k14x-5g-icy-blue-64-gb")
                .brand("OPPO")
                .category(catMap.get("mobiles"))
                .description("4 GB RAM | 64 GB ROM | Expandable Upto 1 TB\n17.15 cm (6.75 inch) HD+ Display\n50MP + 2MP Rear Camera | 5MP Front Camera\n6500 mAh Lithium-ion Battery with 45W SUPERVOOC Fast Charging\nMediaTek Dimensity 6300 Octa Core Processor (2.4 GHz)\nDual Stereo Speakers with Ultra Volume Mode\nIP54 Dust and Water Resistance\n1 Year Manufacturer Warranty for Device and 6 Months Manufacturer Warranty for Inbox Accessories.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85171300")
                .build());

        ProductVariant prod_oppo_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_oppo)
                .sku("OPK14X-IB-64")
                .price(new BigDecimal("14999.0"))
                .compareAtPrice(new BigDecimal("19999.0"))
                .stock(18)
                .attributesJson("{\"color\":\"Icy Blue\",\"storage\":\"64GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_oppo_var_1)
                .imageUrl("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        ProductVariant prod_oppo_var_2 = variantRepository.save(ProductVariant.builder()
                .product(prod_oppo)
                .sku("OPK14X-IB-128")
                .price(new BigDecimal("16999.0"))
                .compareAtPrice(new BigDecimal("17999.0"))
                .stock(12)
                .attributesJson("{\"color\":\"Icy Blue\",\"storage\":\"128GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_oppo_var_2)
                .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        ProductVariant prod_oppo_var_3 = variantRepository.save(ProductVariant.builder()
                .product(prod_oppo)
                .sku("OPK14X-IB-128-6")
                .price(new BigDecimal("18999.0"))
                .compareAtPrice(new BigDecimal("19999.0"))
                .stock(10)
                .attributesJson("{\"color\":\"Icy Blue\",\"storage\":\"128GB + 6GB RAM\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_oppo_var_3)
                .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        ProductVariant prod_oppo_var_4 = variantRepository.save(ProductVariant.builder()
                .product(prod_oppo)
                .sku("OPK14X-MB-64")
                .price(new BigDecimal("14999.0"))
                .compareAtPrice(new BigDecimal("19999.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Midnight Black\",\"storage\":\"64GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_oppo_var_4)
                .imageUrl("https://images.unsplash.com/photo-1523206489230-c012c64b2b48?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: electronics

        Product prod_11 = productRepository.save(Product.builder()
                .title("Dell XPS 15 9530 Laptop")
                .slug("dell-xps-15-9530-laptop")
                .brand("Dell")
                .category(catMap.get("electronics"))
                .description("High performance with Intel Core i7, 32GB RAM, and 1TB SSD for advanced creative work.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("84713010")
                .build());

        ProductVariant prod_11_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_11)
                .sku("XPS15-i7-32G")
                .price(new BigDecimal("189900.0"))
                .compareAtPrice(new BigDecimal("209900.0"))
                .stock(5)
                .attributesJson("{\"processor\":\"Intel i7\",\"ram\":\"32GB\",\"storage\":\"1TB SSD\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_11_var_1)
                .imageUrl("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_12 = productRepository.save(Product.builder()
                .title("MacBook Pro 16 M3 Max")
                .slug("macbook-pro-16-m3-max")
                .brand("Apple")
                .category(catMap.get("electronics"))
                .description("Liquid Retina XDR display, up to 22 hours of battery life, and the groundbreaking M3 Max chip.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("84713010")
                .build());

        ProductVariant prod_12_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_12)
                .sku("MBP16-M3M-48")
                .price(new BigDecimal("349900.0"))
                .compareAtPrice(new BigDecimal("399900.0"))
                .stock(4)
                .attributesJson("{\"chip\":\"M3 Max\",\"ram\":\"48GB\",\"storage\":\"1TB SSD\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_12_var_1)
                .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_13 = productRepository.save(Product.builder()
                .title("Sony WH-1000XM5 Headphones")
                .slug("sony-wh-1000xm5-headphones")
                .brand("Sony")
                .category(catMap.get("electronics"))
                .description("Rewriting the rules for distraction-free listening with industry-leading active noise cancellation.")
                .status("ACTIVE")
                .countryOfOrigin("Japan")
                .hsnCode("85183000")
                .build());

        ProductVariant prod_13_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_13)
                .sku("WH1000XM5-BK")
                .price(new BigDecimal("29900.0"))
                .compareAtPrice(new BigDecimal("34900.0"))
                .stock(35)
                .attributesJson("{\"color\":\"Midnight Black\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_13_var_1)
                .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_14 = productRepository.save(Product.builder()
                .title("Canon EOS R5 Mirrorless Camera")
                .slug("canon-eos-r5-mirrorless-camera")
                .brand("Canon")
                .category(catMap.get("electronics"))
                .description("Professional full-frame mirrorless camera offering 45 Megapixel stills and 8K video recording.")
                .status("ACTIVE")
                .countryOfOrigin("Japan")
                .hsnCode("85258900")
                .build());

        ProductVariant prod_14_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_14)
                .sku("EOSR5-BODY")
                .price(new BigDecimal("329900.0"))
                .compareAtPrice(new BigDecimal("359900.0"))
                .stock(3)
                .attributesJson("{\"model\":\"Body Only\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_14_var_1)
                .imageUrl("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_15 = productRepository.save(Product.builder()
                .title("Apple iPad Pro 12.9 M2")
                .slug("apple-ipad-pro-129-m2")
                .brand("Apple")
                .category(catMap.get("electronics"))
                .description("Brilliant Liquid Retina XDR display, superfast M2 chip, and support for Apple Pencil 2nd gen.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("84713010")
                .build());

        ProductVariant prod_15_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_15)
                .sku("IPADP12.9-M2-256")
                .price(new BigDecimal("119900.0"))
                .compareAtPrice(new BigDecimal("129900.0"))
                .stock(10)
                .attributesJson("{\"color\":\"Space Gray\",\"storage\":\"256GB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_15_var_1)
                .imageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_16 = productRepository.save(Product.builder()
                .title("Logitech MX Master 3S Mouse")
                .slug("logitech-mx-master-3s-mouse")
                .brand("Logitech")
                .category(catMap.get("electronics"))
                .description("An iconic mouse remastered. Quiet clicks and 8K DPI tracking for speed, precision, and comfort.")
                .status("ACTIVE")
                .countryOfOrigin("Switzerland")
                .hsnCode("84716060")
                .build());

        ProductVariant prod_16_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_16)
                .sku("MXM3S-GR")
                .price(new BigDecimal("9995.0"))
                .compareAtPrice(new BigDecimal("10995.0"))
                .stock(45)
                .attributesJson("{\"color\":\"Graphite\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_16_var_1)
                .imageUrl("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_17 = productRepository.save(Product.builder()
                .title("Keychron K2 Mechanical Keyboard")
                .slug("keychron-k2-mechanical-keyboard")
                .brand("Keychron")
                .category(catMap.get("electronics"))
                .description("75% layout wireless mechanical keyboard with Gateron G Pro switches and RGB backlighting.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("84716024")
                .build());

        ProductVariant prod_17_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_17)
                .sku("K2-BR-RGB")
                .price(new BigDecimal("8499.0"))
                .compareAtPrice(new BigDecimal("9999.0"))
                .stock(30)
                .attributesJson("{\"switches\":\"Gateron Brown\",\"backlight\":\"RGB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_17_var_1)
                .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_18 = productRepository.save(Product.builder()
                .title("Asus ROG Zephyrus G14")
                .slug("asus-rog-zephyrus-g14")
                .brand("Asus")
                .category(catMap.get("electronics"))
                .description("Power meets portability. AMD Ryzen 9, 16GB DDR5 RAM, and NVIDIA RTX 4060 graphics.")
                .status("ACTIVE")
                .countryOfOrigin("Taiwan")
                .hsnCode("84713010")
                .build());

        ProductVariant prod_18_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_18)
                .sku("ZEPHYRUS-G14-4060")
                .price(new BigDecimal("149990.0"))
                .compareAtPrice(new BigDecimal("169990.0"))
                .stock(6)
                .attributesJson("{\"processor\":\"Ryzen 9\",\"gpu\":\"RTX 4060\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_18_var_1)
                .imageUrl("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_19 = productRepository.save(Product.builder()
                .title("Seagate Expansion 2TB HDD")
                .slug("seagate-expansion-2tb-hdd")
                .brand("Seagate")
                .category(catMap.get("electronics"))
                .description("Easy-to-use external hard drive. Drag-and-drop file saving right out of the box.")
                .status("ACTIVE")
                .countryOfOrigin("Thailand")
                .hsnCode("84717020")
                .build());

        ProductVariant prod_19_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_19)
                .sku("SEAGATE-EXP-2TB")
                .price(new BigDecimal("5499.0"))
                .compareAtPrice(new BigDecimal("7999.0"))
                .stock(100)
                .attributesJson("{\"capacity\":\"2TB\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_19_var_1)
                .imageUrl("https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_20 = productRepository.save(Product.builder()
                .title("Anker PowerCore 20K Power Bank")
                .slug("anker-powercore-20k-power-bank")
                .brand("Anker")
                .category(catMap.get("electronics"))
                .description("High-capacity portable charger with PowerIQ technology, dual USB-A and USB-C output ports.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85044090")
                .build());

        ProductVariant prod_20_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_20)
                .sku("ANKER-PC-20K")
                .price(new BigDecimal("3299.0"))
                .compareAtPrice(new BigDecimal("4999.0"))
                .stock(120)
                .attributesJson("{\"capacity\":\"20000mAh\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_20_var_1)
                .imageUrl("https://images.unsplash.com/photo-1609592424109-dd9892f1b17c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: tvs-appliances

        Product prod_21 = productRepository.save(Product.builder()
                .title("Samsung Neo QLED 4K TV")
                .slug("samsung-neo-qled-4k-tv")
                .brand("Samsung")
                .category(catMap.get("tvs-appliances"))
                .description("Ultra-precise contrast with Quantum Mini LEDs. Neural Quantum Processor 4K with AI Upscaling.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85287219")
                .build());

        ProductVariant prod_21_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_21)
                .sku("SAMS-55-QLED")
                .price(new BigDecimal("124990.0"))
                .compareAtPrice(new BigDecimal("159900.0"))
                .stock(8)
                .attributesJson("{\"size\":\"55 Inches\",\"resolution\":\"4K Ultra HD\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_21_var_1)
                .imageUrl("https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_22 = productRepository.save(Product.builder()
                .title("LG C3 55-inch OLED TV")
                .slug("lg-c3-55-inch-oled-tv")
                .brand("LG")
                .category(catMap.get("tvs-appliances"))
                .description("OLED Evo displaying infinite contrast, 100% color volume, and advanced gaming features (HDMI 2.1).")
                .status("ACTIVE")
                .countryOfOrigin("South Korea")
                .hsnCode("85287219")
                .build());

        ProductVariant prod_22_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_22)
                .sku("LG-55-C3OLED")
                .price(new BigDecimal("149990.0"))
                .compareAtPrice(new BigDecimal("189900.0"))
                .stock(5)
                .attributesJson("{\"size\":\"55 Inches\",\"panel\":\"OLED Evo\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_22_var_1)
                .imageUrl("https://images.unsplash.com/photo-1552975084-6e027cd345c2?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_23 = productRepository.save(Product.builder()
                .title("Dyson V15 Cordless Vacuum")
                .slug("dyson-v15-cordless-vacuum")
                .brand("Dyson")
                .category(catMap.get("tvs-appliances"))
                .description("Intelligent cordless vacuum with laser illumination. Quantifies and counts picked-up particles.")
                .status("ACTIVE")
                .countryOfOrigin("Malaysia")
                .hsnCode("85081100")
                .build());

        ProductVariant prod_23_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_23)
                .sku("DYSON-V15-CORDLESS")
                .price(new BigDecimal("65900.0"))
                .compareAtPrice(new BigDecimal("74900.0"))
                .stock(15)
                .attributesJson("{\"model\":\"V15 Detect\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_23_var_1)
                .imageUrl("https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_24 = productRepository.save(Product.builder()
                .title("Philips Digital Air Fryer")
                .slug("philips-digital-air-fryer")
                .brand("Philips")
                .category(catMap.get("tvs-appliances"))
                .description("Healthy frying with Rapid Air technology. 90% less fat. Premium digital touch panel with 7 presets.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("85166090")
                .build());

        ProductVariant prod_24_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_24)
                .sku("PHILIPS-AF-9252")
                .price(new BigDecimal("8999.0"))
                .compareAtPrice(new BigDecimal("12999.0"))
                .stock(40)
                .attributesJson("{\"capacity\":\"4.1L\",\"power\":\"1400W\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_24_var_1)
                .imageUrl("https://images.unsplash.com/photo-1621972750749-0fbb1abb7736?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_25 = productRepository.save(Product.builder()
                .title("IFB 8kg Front Load Washing Machine")
                .slug("ifb-8kg-front-load-washing-machine")
                .brand("IFB")
                .category(catMap.get("tvs-appliances"))
                .description("Fully automatic front-load washer featuring 5-star rating, Aqua Energie filter, and steam wash cycle.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("84501190")
                .build());

        ProductVariant prod_25_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_25)
                .sku("IFB-SEN-8KG")
                .price(new BigDecimal("36990.0"))
                .compareAtPrice(new BigDecimal("42990.0"))
                .stock(12)
                .attributesJson("{\"capacity\":\"8kg\",\"color\":\"Silver\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_25_var_1)
                .imageUrl("https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_26 = productRepository.save(Product.builder()
                .title("Bosch 13 Place Dishwasher")
                .slug("bosch-13-place-dishwasher")
                .brand("Bosch")
                .category(catMap.get("tvs-appliances"))
                .description("Engineered for Indian kitchens. Easily fits large pots and pans. 6 wash programs with Eco mode.")
                .status("ACTIVE")
                .countryOfOrigin("Turkey")
                .hsnCode("84221100")
                .build());

        ProductVariant prod_26_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_26)
                .sku("BOSCH-DW-SMS2")
                .price(new BigDecimal("41990.0"))
                .compareAtPrice(new BigDecimal("48990.0"))
                .stock(8)
                .attributesJson("{\"capacity\":\"13 Place\",\"color\":\"Silver\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_26_var_1)
                .imageUrl("https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_27 = productRepository.save(Product.builder()
                .title("Samsung Double Door Refrigerator")
                .slug("samsung-double-door-refrigerator")
                .brand("Samsung")
                .category(catMap.get("tvs-appliances"))
                .description("Frost-free double door refrigerator with convertible 5-in-1 modes and digital inverter technology.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("84181010")
                .build());

        ProductVariant prod_27_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_27)
                .sku("SAMS-REF-324L")
                .price(new BigDecimal("34990.0"))
                .compareAtPrice(new BigDecimal("41990.0"))
                .stock(10)
                .attributesJson("{\"capacity\":\"324L\",\"starRating\":\"3 Star\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_27_var_1)
                .imageUrl("https://images.unsplash.com/photo-1571175432274-569614457ee4?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_28 = productRepository.save(Product.builder()
                .title("Panasonic Convection Microwave")
                .slug("panasonic-convection-microwave")
                .brand("Panasonic")
                .category(catMap.get("tvs-appliances"))
                .description("Convection microwave oven with 27L capacity, 360-degree heat wrap, and auto-cook menus.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85165000")
                .build());

        ProductVariant prod_28_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_28)
                .sku("PANA-MWO-27L")
                .price(new BigDecimal("12990.0"))
                .compareAtPrice(new BigDecimal("16990.0"))
                .stock(20)
                .attributesJson("{\"capacity\":\"27L\",\"type\":\"Convection\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_28_var_1)
                .imageUrl("https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_29 = productRepository.save(Product.builder()
                .title("Kent Grand Plus RO Water Purifier")
                .slug("kent-grand-plus-ro-water-purifier")
                .brand("Kent")
                .category(catMap.get("tvs-appliances"))
                .description("Wall-mountable RO+UV+UF water purifier with TDS controller. Fully automatic operation.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("84212190")
                .build());

        ProductVariant prod_29_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_29)
                .sku("KENT-GP-RO")
                .price(new BigDecimal("16490.0"))
                .compareAtPrice(new BigDecimal("19500.0"))
                .stock(30)
                .attributesJson("{\"capacity\":\"8L\",\"purification\":\"RO+UV+UF\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_29_var_1)
                .imageUrl("https://images.unsplash.com/photo-1609766918204-c5a4a5bbce35?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_30 = productRepository.save(Product.builder()
                .title("Daikin 1.5 Ton 5-Star Split AC")
                .slug("daikin-15-ton-5-star-split-ac")
                .brand("Daikin")
                .category(catMap.get("tvs-appliances"))
                .description("Inverter split AC with PM2.5 filter, copper condenser, and Coanda airflow for optimal cooling.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("84151010")
                .build());

        ProductVariant prod_30_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_30)
                .sku("DAIKIN-AC-1.5T")
                .price(new BigDecimal("45990.0"))
                .compareAtPrice(new BigDecimal("56990.0"))
                .stock(15)
                .attributesJson("{\"capacity\":\"1.5 Ton\",\"starRating\":\"5 Star\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_30_var_1)
                .imageUrl("https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: men

        Product prod_31 = productRepository.save(Product.builder()
                .title("Levi's 511 Slim Fit Jeans")
                .slug("levis-511-slim-fit-jeans")
                .brand("Levi's")
                .category(catMap.get("men"))
                .description("Classic slim-fit jeans designed to sit below the waist with a slim leg from hip to ankle.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("62034200")
                .build());

        ProductVariant prod_31_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_31)
                .sku("LEVIS-511-32")
                .price(new BigDecimal("2499.0"))
                .compareAtPrice(new BigDecimal("3999.0"))
                .stock(50)
                .attributesJson("{\"size\":\"32\",\"color\":\"Dark Blue\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_31_var_1)
                .imageUrl("https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_32 = productRepository.save(Product.builder()
                .title("Tommy Hilfiger Polo T-Shirt")
                .slug("tommy-hilfiger-polo-t-shirt")
                .brand("Tommy Hilfiger")
                .category(catMap.get("men"))
                .description("Premium quality 100% cotton polo t-shirt with classic signature flag embroidery on the chest.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("61091000")
                .build());

        ProductVariant prod_32_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_32)
                .sku("TOMMY-POLO-M")
                .price(new BigDecimal("1999.0"))
                .compareAtPrice(new BigDecimal("2999.0"))
                .stock(40)
                .attributesJson("{\"size\":\"M\",\"color\":\"Classic Navy\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_32_var_1)
                .imageUrl("https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_33 = productRepository.save(Product.builder()
                .title("Nike Air Max Running Shoes")
                .slug("nike-air-max-running-shoes")
                .brand("Nike")
                .category(catMap.get("men"))
                .description("Maximum cushioning with lightweight breathable mesh. Perfect for runs or casual streetwear.")
                .status("ACTIVE")
                .countryOfOrigin("Vietnam")
                .hsnCode("64041190")
                .build());

        ProductVariant prod_33_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_33)
                .sku("NIKE-AM-UK8")
                .price(new BigDecimal("8995.0"))
                .compareAtPrice(new BigDecimal("10995.0"))
                .stock(25)
                .attributesJson("{\"size\":\"UK 8\",\"color\":\"Bright Red\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_33_var_1)
                .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_34 = productRepository.save(Product.builder()
                .title("Allen Solly Formal Cotton Shirt")
                .slug("allen-solly-formal-cotton-shirt")
                .brand("Allen Solly")
                .category(catMap.get("men"))
                .description("100% cotton slim-fit formal shirt. Ideal for corporate daily wear or special formal dinners.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("62052000")
                .build());

        ProductVariant prod_34_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_34)
                .sku("AS-FORMAL-40")
                .price(new BigDecimal("1499.0"))
                .compareAtPrice(new BigDecimal("2299.0"))
                .stock(35)
                .attributesJson("{\"size\":\"40\",\"color\":\"White\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_34_var_1)
                .imageUrl("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_35 = productRepository.save(Product.builder()
                .title("Adidas Hooded Sweatshirt")
                .slug("adidas-hooded-sweatshirt")
                .brand("Adidas")
                .category(catMap.get("men"))
                .description("Classic fleece pullover hoodie featuring the iconic Adidas Trefoil screen-printed on the chest.")
                .status("ACTIVE")
                .countryOfOrigin("Cambodia")
                .hsnCode("61103030")
                .build());

        ProductVariant prod_35_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_35)
                .sku("ADI-HOOD-L")
                .price(new BigDecimal("3299.0"))
                .compareAtPrice(new BigDecimal("4999.0"))
                .stock(20)
                .attributesJson("{\"size\":\"L\",\"color\":\"Grey\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_35_var_1)
                .imageUrl("https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_36 = productRepository.save(Product.builder()
                .title("Puma Sport Running Shoes")
                .slug("puma-sport-running-shoes")
                .brand("Puma")
                .category(catMap.get("men"))
                .description("SoftFoam+ sockliner for superior cushioning and optimal comfort during everyday activities.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("64041190")
                .build());

        ProductVariant prod_36_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_36)
                .sku("PUMA-RUN-UK9")
                .price(new BigDecimal("3499.0"))
                .compareAtPrice(new BigDecimal("5999.0"))
                .stock(30)
                .attributesJson("{\"size\":\"UK 9\",\"color\":\"Grey-Black\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_36_var_1)
                .imageUrl("https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_37 = productRepository.save(Product.builder()
                .title("Raymond Men's Premium Suit")
                .slug("raymond-mens-premium-suit")
                .brand("Raymond")
                .category(catMap.get("men"))
                .description("Elegant 2-piece dark grey suit. Designed with precision, offering a sharp tailored look.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("62031100")
                .build());

        ProductVariant prod_37_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_37)
                .sku("RAY-SUIT-40")
                .price(new BigDecimal("8999.0"))
                .compareAtPrice(new BigDecimal("12999.0"))
                .stock(10)
                .attributesJson("{\"size\":\"40\",\"color\":\"Charcoal Grey\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_37_var_1)
                .imageUrl("https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_38 = productRepository.save(Product.builder()
                .title("Casio Edifice Chronograph Watch")
                .slug("casio-edifice-chronograph-watch")
                .brand("Casio")
                .category(catMap.get("men"))
                .description("Stunning analog chronograph watch with solid stainless steel band, stopwatch, and 100m water resistance.")
                .status("ACTIVE")
                .countryOfOrigin("Japan")
                .hsnCode("91021100")
                .build());

        ProductVariant prod_38_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_38)
                .sku("CASIO-ED-SILVER")
                .price(new BigDecimal("11995.0"))
                .compareAtPrice(new BigDecimal("13995.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Silver Steel\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_38_var_1)
                .imageUrl("https://images.unsplash.com/photo-1522312346375-d1a52e2b99b3?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_39 = productRepository.save(Product.builder()
                .title("Woodland Leather Hiking Boots")
                .slug("woodland-leather-hiking-boots")
                .brand("Woodland")
                .category(catMap.get("men"))
                .description("High ankle nubuck leather boots. Extremely durable and provides exceptional grip on all terrains.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("64035190")
                .build());

        ProductVariant prod_39_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_39)
                .sku("WOOD-BOOT-UK8")
                .price(new BigDecimal("4295.0"))
                .compareAtPrice(new BigDecimal("5495.0"))
                .stock(18)
                .attributesJson("{\"size\":\"UK 8\",\"color\":\"Olive Green\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_39_var_1)
                .imageUrl("https://images.unsplash.com/photo-1520639888713-7851133b1ed0?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_40 = productRepository.save(Product.builder()
                .title("Ray-Ban Classic Sunglasses")
                .slug("ray-ban-classic-sunglasses")
                .brand("Ray-Ban")
                .category(catMap.get("men"))
                .description("Aviator Classic sunglasses. Originally designed for U.S. aviators in 1937, offering exceptional clarity.")
                .status("ACTIVE")
                .countryOfOrigin("Italy")
                .hsnCode("90041000")
                .build());

        ProductVariant prod_40_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_40)
                .sku("RAY-AV-GREEN")
                .price(new BigDecimal("8490.0"))
                .compareAtPrice(new BigDecimal("9990.0"))
                .stock(25)
                .attributesJson("{\"color\":\"Gold / Green G-15\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_40_var_1)
                .imageUrl("https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: women

        Product prod_41 = productRepository.save(Product.builder()
                .title("Zara Floral Print Dress")
                .slug("zara-floral-print-dress")
                .brand("Zara")
                .category(catMap.get("women"))
                .description("Beautiful flowy midi dress with a V-neckline, long sleeves, and a colorful floral print design.")
                .status("ACTIVE")
                .countryOfOrigin("Spain")
                .hsnCode("62044200")
                .build());

        ProductVariant prod_41_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_41)
                .sku("ZARA-DRESS-S")
                .price(new BigDecimal("3990.0"))
                .compareAtPrice(new BigDecimal("5990.0"))
                .stock(15)
                .attributesJson("{\"size\":\"S\",\"color\":\"Floral\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_41_var_1)
                .imageUrl("https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_42 = productRepository.save(Product.builder()
                .title("Biba Cotton Anarkali Kurta")
                .slug("biba-cotton-anarkali-kurta")
                .brand("Biba")
                .category(catMap.get("women"))
                .description("Elegant printed cotton Anarkali suit set with matching dupatta and narrow trousers.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("62114210")
                .build());

        ProductVariant prod_42_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_42)
                .sku("BIBA-KURTA-34")
                .price(new BigDecimal("2999.0"))
                .compareAtPrice(new BigDecimal("4599.0"))
                .stock(20)
                .attributesJson("{\"size\":\"34\",\"color\":\"Pink\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_42_var_1)
                .imageUrl("https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_43 = productRepository.save(Product.builder()
                .title("Levi's Women Super Skinny Jeans")
                .slug("levis-women-super-skinny-jeans")
                .brand("Levi's")
                .category(catMap.get("women"))
                .description("Mid-rise super-skinny fit jeans designed to hold, lift, and flatter your shape all day.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("62046200")
                .build());

        ProductVariant prod_43_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_43)
                .sku("LEVIS-FEM-28")
                .price(new BigDecimal("1999.0"))
                .compareAtPrice(new BigDecimal("2999.0"))
                .stock(35)
                .attributesJson("{\"size\":\"28\",\"color\":\"Indigo Blue\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_43_var_1)
                .imageUrl("https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_44 = productRepository.save(Product.builder()
                .title("H&M Cable Knit Sweater")
                .slug("handm-cable-knit-sweater")
                .brand("H&M")
                .category(catMap.get("women"))
                .description("Cozy loose-fit cable-knit sweater made of soft yarn containing wool. Ribbed neck, hem, and cuffs.")
                .status("ACTIVE")
                .countryOfOrigin("Bangladesh")
                .hsnCode("61101110")
                .build());

        ProductVariant prod_44_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_44)
                .sku("HM-SWEATER-M")
                .price(new BigDecimal("1799.0"))
                .compareAtPrice(new BigDecimal("2499.0"))
                .stock(25)
                .attributesJson("{\"size\":\"M\",\"color\":\"Beige\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_44_var_1)
                .imageUrl("https://images.unsplash.com/photo-1574169208507-84376144848b?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_45 = productRepository.save(Product.builder()
                .title("Michael Kors Saffiano Tote Bag")
                .slug("michael-kors-saffiano-tote-bag")
                .brand("Michael Kors")
                .category(catMap.get("women"))
                .description("Classic saffiano leather tote bag featuring a spacious interior, zippered pockets, and signature gold pendant.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("42022100")
                .build());

        ProductVariant prod_45_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_45)
                .sku("MK-TOTE-BROWN")
                .price(new BigDecimal("16900.0"))
                .compareAtPrice(new BigDecimal("24900.0"))
                .stock(8)
                .attributesJson("{\"color\":\"Acorn Brown\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_45_var_1)
                .imageUrl("https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_46 = productRepository.save(Product.builder()
                .title("Fossil Stella Analog Watch")
                .slug("fossil-stella-analog-watch")
                .brand("Fossil")
                .category(catMap.get("women"))
                .description("Women's Stella multifunction quartz watch featuring a rose-gold dial with crystal bezel embellishments.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("91021100")
                .build());

        ProductVariant prod_46_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_46)
                .sku("FOSSIL-STELLA-RG")
                .price(new BigDecimal("9995.0"))
                .compareAtPrice(new BigDecimal("12495.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Rose Gold\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_46_var_1)
                .imageUrl("https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_47 = productRepository.save(Product.builder()
                .title("Nike Court Legacy Sneakers")
                .slug("nike-court-legacy-sneakers")
                .brand("Nike")
                .category(catMap.get("women"))
                .description("Court-inspired classic leather sneakers. Clean design, retro details, and supreme comfort.")
                .status("ACTIVE")
                .countryOfOrigin("Indonesia")
                .hsnCode("64041190")
                .build());

        ProductVariant prod_47_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_47)
                .sku("NIKE-CL-UK5")
                .price(new BigDecimal("5495.0"))
                .compareAtPrice(new BigDecimal("6495.0"))
                .stock(18)
                .attributesJson("{\"size\":\"UK 5\",\"color\":\"White-Pink\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_47_var_1)
                .imageUrl("https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_48 = productRepository.save(Product.builder()
                .title("Aldo Block Heels")
                .slug("aldo-block-heels")
                .brand("Aldo")
                .category(catMap.get("women"))
                .description("Stunning square-toe dress sandals featuring comfortable block heels and double strap design.")
                .status("ACTIVE")
                .countryOfOrigin("Vietnam")
                .hsnCode("64039190")
                .build());

        ProductVariant prod_48_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_48)
                .sku("ALDO-BH-UK6")
                .price(new BigDecimal("5999.0"))
                .compareAtPrice(new BigDecimal("7999.0"))
                .stock(12)
                .attributesJson("{\"size\":\"UK 6\",\"color\":\"Nude\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_48_var_1)
                .imageUrl("https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_49 = productRepository.save(Product.builder()
                .title("Swarovski Crystal Necklace")
                .slug("swarovski-crystal-necklace")
                .brand("Swarovski")
                .category(catMap.get("women"))
                .description("Dazzling round pendant necklace featuring blue and clear Swarovski crystals on a rhodium-plated chain.")
                .status("ACTIVE")
                .countryOfOrigin("Austria")
                .hsnCode("71171900")
                .build());

        ProductVariant prod_49_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_49)
                .sku("SWARO-NECK-BLUE")
                .price(new BigDecimal("7990.0"))
                .compareAtPrice(new BigDecimal("9990.0"))
                .stock(20)
                .attributesJson("{\"color\":\"Silver/Blue\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_49_var_1)
                .imageUrl("https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_50 = productRepository.save(Product.builder()
                .title("MAC Matte Velvet Lipstick")
                .slug("mac-matte-velvet-lipstick")
                .brand("MAC")
                .category(catMap.get("women"))
                .description("The iconic matte lipstick that made MAC famous. Long-wearing, rich pigment color payoff.")
                .status("ACTIVE")
                .countryOfOrigin("Canada")
                .hsnCode("33041000")
                .build());

        ProductVariant prod_50_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_50)
                .sku("MAC-LIP-RUBY")
                .price(new BigDecimal("2150.0"))
                .compareAtPrice(new BigDecimal("2500.0"))
                .stock(50)
                .attributesJson("{\"shade\":\"Ruby Woo\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_50_var_1)
                .imageUrl("https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: baby-kids

        Product prod_51 = productRepository.save(Product.builder()
                .title("Lego Classic Creative Bricks")
                .slug("lego-classic-creative-bricks")
                .brand("LEGO")
                .category(catMap.get("baby-kids"))
                .description("Build up a storm with this big box of classic LEGO bricks in 33 different colors.")
                .status("ACTIVE")
                .countryOfOrigin("Denmark")
                .hsnCode("95030030")
                .build());

        ProductVariant prod_51_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_51)
                .sku("LEGO-CLASSIC-10696")
                .price(new BigDecimal("2499.0"))
                .compareAtPrice(new BigDecimal("2999.0"))
                .stock(30)
                .attributesJson("{\"model\":\"10696 Brick Box\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_51_var_1)
                .imageUrl("https://images.unsplash.com/photo-1585366119957-e57b84bbfa00?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_52 = productRepository.save(Product.builder()
                .title("Hot Wheels 9-Car Gift Pack")
                .slug("hot-wheels-9-car-gift-pack")
                .brand("Hot Wheels")
                .category(catMap.get("baby-kids"))
                .description("Speed into an instant Hot Wheels collection with a 9-pack of highly detailed 1:64 scale die-cast vehicles.")
                .status("ACTIVE")
                .countryOfOrigin("Malaysia")
                .hsnCode("95030090")
                .build());

        ProductVariant prod_52_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_52)
                .sku("HW-9PACK-TOY")
                .price(new BigDecimal("1199.0"))
                .compareAtPrice(new BigDecimal("1499.0"))
                .stock(45)
                .attributesJson("{\"pack\":\"9 Vehicles\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_52_var_1)
                .imageUrl("https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_53 = productRepository.save(Product.builder()
                .title("Barbie Dreamhouse Playset")
                .slug("barbie-dreamhouse-playset")
                .brand("Barbie")
                .category(catMap.get("baby-kids"))
                .description("Featuring 3 stories, 8 rooms, slide, elevator, and pool. Over 70 accessories for endless storytelling.")
                .status("ACTIVE")
                .countryOfOrigin("Indonesia")
                .hsnCode("95030020")
                .build());

        ProductVariant prod_53_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_53)
                .sku("BARBIE-DH-PLAY")
                .price(new BigDecimal("9999.0"))
                .compareAtPrice(new BigDecimal("12999.0"))
                .stock(5)
                .attributesJson("{\"model\":\"Dreamhouse\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_53_var_1)
                .imageUrl("https://images.unsplash.com/photo-1559251606-c623743a6d76?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_54 = productRepository.save(Product.builder()
                .title("Chicco Stroller Echo")
                .slug("chicco-stroller-echo")
                .brand("Chicco")
                .category(catMap.get("baby-kids"))
                .description("A lightweight, compact stroller. Comfort-padded reclining seat and easy folding mechanism.")
                .status("ACTIVE")
                .countryOfOrigin("Italy")
                .hsnCode("87150000")
                .build());

        ProductVariant prod_54_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_54)
                .sku("CHICCO-STROLL-BLUE")
                .price(new BigDecimal("8990.0"))
                .compareAtPrice(new BigDecimal("10990.0"))
                .stock(8)
                .attributesJson("{\"color\":\"Stone Blue\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_54_var_1)
                .imageUrl("https://images.unsplash.com/photo-1591088398332-8a7791972843?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_55 = productRepository.save(Product.builder()
                .title("Carter's Baby Bodysuits 5-Pack")
                .slug("carters-baby-bodysuits-5-pack")
                .brand("Carter's")
                .category(catMap.get("baby-kids"))
                .description("Super soft 100% rib cotton bodysuits. Nickel-free snaps on reinforced panels.")
                .status("ACTIVE")
                .countryOfOrigin("Cambodia")
                .hsnCode("61112000")
                .build());

        ProductVariant prod_55_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_55)
                .sku("CARTERS-BODY-6M")
                .price(new BigDecimal("1499.0"))
                .compareAtPrice(new BigDecimal("2199.0"))
                .stock(40)
                .attributesJson("{\"size\":\"6 Months\",\"pack\":\"5 Pieces\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_55_var_1)
                .imageUrl("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_56 = productRepository.save(Product.builder()
                .title("Pampers Active Baby Diapers")
                .slug("pampers-active-baby-diapers")
                .brand("Pampers")
                .category(catMap.get("baby-kids"))
                .description("Tape-style soft diapers with stretchable sides. Magic Gel lock technology provides up to 12 hours dryness.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("96190010")
                .build());

        ProductVariant prod_56_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_56)
                .sku("PAMPERS-M-76")
                .price(new BigDecimal("999.0"))
                .compareAtPrice(new BigDecimal("1299.0"))
                .stock(100)
                .attributesJson("{\"size\":\"Medium\",\"pack\":\"76 Count\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_56_var_1)
                .imageUrl("https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_57 = productRepository.save(Product.builder()
                .title("Fisher-Price Kick & Play Gym")
                .slug("fisher-price-kick-and-play-gym")
                .brand("Fisher-Price")
                .category(catMap.get("baby-kids"))
                .description("Baby playmat with toy arch, light-up musical piano. Encourages motor skills and sensory learning.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("95030090")
                .build());

        ProductVariant prod_57_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_57)
                .sku("FP-PLAYGYM-PIANO")
                .price(new BigDecimal("3499.0"))
                .compareAtPrice(new BigDecimal("4499.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Multicolor\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_57_var_1)
                .imageUrl("https://images.unsplash.com/photo-1515488042361-404e9250afef?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_58 = productRepository.save(Product.builder()
                .title("LuvLap Wooden Baby Cot")
                .slug("luvlap-wooden-baby-cot")
                .brand("LuvLap")
                .category(catMap.get("baby-kids"))
                .description("Constructed from New Zealand pine wood. Features height adjustments and rolling lockable wheels.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94035000")
                .build());

        ProductVariant prod_58_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_58)
                .sku("LUVLAP-COT-WOOD")
                .price(new BigDecimal("7999.0"))
                .compareAtPrice(new BigDecimal("9999.0"))
                .stock(6)
                .attributesJson("{\"color\":\"Natural Pine\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_58_var_1)
                .imageUrl("https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_59 = productRepository.save(Product.builder()
                .title("Wildcraft Kids School Bag")
                .slug("wildcraft-kids-school-bag")
                .brand("Wildcraft")
                .category(catMap.get("baby-kids"))
                .description("Ergonomic two-compartment backpack designed for junior school kids. Soft cushioning.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("42029210")
                .build());

        ProductVariant prod_59_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_59)
                .sku("WILD-BAG-KIDS")
                .price(new BigDecimal("1299.0"))
                .compareAtPrice(new BigDecimal("1899.0"))
                .stock(25)
                .attributesJson("{\"capacity\":\"20L\",\"color\":\"Blue-Orange\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_59_var_1)
                .imageUrl("https://images.unsplash.com/photo-1576267423445-b2e0074d68a4?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_60 = productRepository.save(Product.builder()
                .title("Nerf Elite 2.0 Commander Blaster")
                .slug("nerf-elite-20-commander-blaster")
                .brand("Nerf")
                .category(catMap.get("baby-kids"))
                .description("Includes rotating drum that holds 6 darts. Shoots darts up to 90 feet. Includes 12 official darts.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("95030090")
                .build());

        ProductVariant prod_60_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_60)
                .sku("NERF-ELITE-CMD")
                .price(new BigDecimal("999.0"))
                .compareAtPrice(new BigDecimal("1499.0"))
                .stock(50)
                .attributesJson("{\"model\":\"Commander RD-6\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_60_var_1)
                .imageUrl("https://images.unsplash.com/photo-1531651008558-ed1740375b39?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: home-furniture

        Product prod_61 = productRepository.save(Product.builder()
                .title("Sleepwell Ortho Mattress")
                .slug("sleepwell-ortho-mattress")
                .brand("Sleepwell")
                .category(catMap.get("home-furniture"))
                .description("Orthopedic memory foam mattress with triple-zoned profile support for back pain relief.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94042110")
                .build());

        ProductVariant prod_61_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_61)
                .sku("SLEEP-ORTHO-QUEEN")
                .price(new BigDecimal("14999.0"))
                .compareAtPrice(new BigDecimal("18999.0"))
                .stock(10)
                .attributesJson("{\"dimensions\":\"72x60x6 inches\",\"size\":\"Queen\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_61_var_1)
                .imageUrl("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_62 = productRepository.save(Product.builder()
                .title("Wooden Coffee Table")
                .slug("wooden-coffee-table")
                .brand("Wooden Street")
                .category(catMap.get("home-furniture"))
                .description("Solid Sheesham wood coffee table in honey finish, featuring integrated drawer storage.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94036000")
                .build());

        ProductVariant prod_62_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_62)
                .sku("WOODST-COFFEE-TAB")
                .price(new BigDecimal("7499.0"))
                .compareAtPrice(new BigDecimal("9999.0"))
                .stock(12)
                .attributesJson("{\"wood\":\"Sheesham Wood\",\"finish\":\"Honey\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_62_var_1)
                .imageUrl("https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_63 = productRepository.save(Product.builder()
                .title("L-Shaped Sectional Sofa")
                .slug("l-shaped-sectional-sofa")
                .brand("Home Centre")
                .category(catMap.get("home-furniture"))
                .description("Modern fabric sectional sofa. Reversible orientation with premium foam cushion seating.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94016100")
                .build());

        ProductVariant prod_63_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_63)
                .sku("HC-SOFA-LSHAPE")
                .price(new BigDecimal("32990.0"))
                .compareAtPrice(new BigDecimal("45990.0"))
                .stock(4)
                .attributesJson("{\"color\":\"Slate Grey\",\"material\":\"Fabric\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_63_var_1)
                .imageUrl("https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_64 = productRepository.save(Product.builder()
                .title("Solimo Fabric Recliner Chair")
                .slug("solimo-fabric-recliner-chair")
                .brand("Solimo")
                .category(catMap.get("home-furniture"))
                .description("Single-seater recliner chair made of high-quality fabric in attractive chocolate finish.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94016100")
                .build());

        ProductVariant prod_64_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_64)
                .sku("SOLIMO-REC-BROWN")
                .price(new BigDecimal("15999.0"))
                .compareAtPrice(new BigDecimal("22000.0"))
                .stock(15)
                .attributesJson("{\"color\":\"Brown\",\"type\":\"Manual\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_64_var_1)
                .imageUrl("https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_65 = productRepository.save(Product.builder()
                .title("Wipro Smart LED Bulb 12W")
                .slug("wipro-smart-led-bulb-12w")
                .brand("Wipro")
                .category(catMap.get("home-furniture"))
                .description("Wi-Fi enabled smart LED bulb compatible with Alexa and Google Assistant. 16 million colors.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("85395210")
                .build());

        ProductVariant prod_65_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_65)
                .sku("WIPRO-SMART-12W")
                .price(new BigDecimal("699.0"))
                .compareAtPrice(new BigDecimal("1299.0"))
                .stock(150)
                .attributesJson("{\"base\":\"B22\",\"power\":\"12W\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_65_var_1)
                .imageUrl("https://images.unsplash.com/photo-1550985616-10810253b84d?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_66 = productRepository.save(Product.builder()
                .title("Philips Hue Starter Kit")
                .slug("philips-hue-starter-kit")
                .brand("Philips")
                .category(catMap.get("home-furniture"))
                .description("Includes three Hue white and color ambiance smart bulbs and Hue Bridge router.")
                .status("ACTIVE")
                .countryOfOrigin("Poland")
                .hsnCode("94054200")
                .build());

        ProductVariant prod_66_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_66)
                .sku("PHILIPS-HUE-KIT")
                .price(new BigDecimal("9999.0"))
                .compareAtPrice(new BigDecimal("13999.0"))
                .stock(20)
                .attributesJson("{\"base\":\"E27\",\"pack\":\"3 Bulbs + Bridge\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_66_var_1)
                .imageUrl("https://images.unsplash.com/photo-1565814329452-e1efa11c5b89?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_67 = productRepository.save(Product.builder()
                .title("Tupperware Storage Set")
                .slug("tupperware-storage-set")
                .brand("Tupperware")
                .category(catMap.get("home-furniture"))
                .description("A set of 4 classic airtight storage containers keeping dry food fresh and moisture-free.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("39241010")
                .build());

        ProductVariant prod_67_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_67)
                .sku("TUPP-STOR-4PC")
                .price(new BigDecimal("1099.0"))
                .compareAtPrice(new BigDecimal("1499.0"))
                .stock(80)
                .attributesJson("{\"pack\":\"4 Containers\",\"capacity\":\"1.1L\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_67_var_1)
                .imageUrl("https://images.unsplash.com/photo-1606787366850-de6330128bfc?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_68 = productRepository.save(Product.builder()
                .title("Nilkamal Plastic Chair Pack of 4")
                .slug("nilkamal-plastic-chair-pack-of-4")
                .brand("Nilkamal")
                .category(catMap.get("home-furniture"))
                .description("Durable heavy-duty plastic chairs for indoor and outdoor seating. Stackable design.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("94018000")
                .build());

        ProductVariant prod_68_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_68)
                .sku("NILK-CHAIR-4PC")
                .price(new BigDecimal("2499.0"))
                .compareAtPrice(new BigDecimal("3499.0"))
                .stock(40)
                .attributesJson("{\"color\":\"Marble Beige\",\"pack\":\"4 Chairs\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_68_var_1)
                .imageUrl("https://images.unsplash.com/photo-1506439773649-6e0eb8cfb237?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_69 = productRepository.save(Product.builder()
                .title("Prestige Cookware Set")
                .slug("prestige-cookware-set")
                .brand("Prestige")
                .category(catMap.get("home-furniture"))
                .description("Three-piece non-stick cookware set including kadai, frying pan, and omni tawa with glass lid.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("76151011")
                .build());

        ProductVariant prod_69_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_69)
                .sku("PREST-NS-3PC")
                .price(new BigDecimal("1999.0"))
                .compareAtPrice(new BigDecimal("2999.0"))
                .stock(60)
                .attributesJson("{\"pack\":\"3 Pieces\",\"material\":\"Aluminum\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_69_var_1)
                .imageUrl("https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_70 = productRepository.save(Product.builder()
                .title("Bombay Dyeing Bed Sheet")
                .slug("bombay-dyeing-bed-sheet")
                .brand("Bombay Dyeing")
                .category(catMap.get("home-furniture"))
                .description("100% cotton double bedsheet with 2 matching pillow covers. Floral design in high thread count.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("63041910")
                .build());

        ProductVariant prod_70_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_70)
                .sku("BD-SHEET-DOUBLE")
                .price(new BigDecimal("1299.0"))
                .compareAtPrice(new BigDecimal("1899.0"))
                .stock(50)
                .attributesJson("{\"size\":\"Double Bed\",\"material\":\"Cotton\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_70_var_1)
                .imageUrl("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: sports-books

        Product prod_71 = productRepository.save(Product.builder()
                .title("Yonex Carbonex Racket")
                .slug("yonex-carbonex-racket")
                .brand("Yonex")
                .category(catMap.get("sports-books"))
                .description("Carbonex Series badminton racket with full cover. Great for intermediate players.")
                .status("ACTIVE")
                .countryOfOrigin("Japan")
                .hsnCode("95065100")
                .build());

        ProductVariant prod_71_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_71)
                .sku("YONEX-CAB-8000")
                .price(new BigDecimal("1899.0"))
                .compareAtPrice(new BigDecimal("2499.0"))
                .stock(45)
                .attributesJson("{\"color\":\"Red\",\"material\":\"Carbon Graphite\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_71_var_1)
                .imageUrl("https://images.unsplash.com/photo-1613918431208-6752fe243431?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_72 = productRepository.save(Product.builder()
                .title("Cosco Cricket Bat & Ball Set")
                .slug("cosco-cricket-bat-and-ball-set")
                .brand("Cosco")
                .category(catMap.get("sports-books"))
                .description("Popular Willow cricket bat with premium rubber grip and 2 tennis balls. Perfect for recreational street play.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("95069990")
                .build());

        ProductVariant prod_72_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_72)
                .sku("COSCO-CRIC-SET")
                .price(new BigDecimal("1499.0"))
                .compareAtPrice(new BigDecimal("1999.0"))
                .stock(35)
                .attributesJson("{\"batSize\":\"5\",\"wood\":\"Popular Willow\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_72_var_1)
                .imageUrl("https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_73 = productRepository.save(Product.builder()
                .title("Decathlon Quechua Backpack")
                .slug("decathlon-quechua-backpack")
                .brand("Decathlon")
                .category(catMap.get("sports-books"))
                .description("Hiking backpack 20L. Light, sturdy, comfortable straps, and numerous smart storage slots.")
                .status("ACTIVE")
                .countryOfOrigin("Vietnam")
                .hsnCode("42029220")
                .build());

        ProductVariant prod_73_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_73)
                .sku("DEC-QUE-20L")
                .price(new BigDecimal("999.0"))
                .compareAtPrice(new BigDecimal("1499.0"))
                .stock(80)
                .attributesJson("{\"capacity\":\"20L\",\"color\":\"Black-Grey\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_73_var_1)
                .imageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_74 = productRepository.save(Product.builder()
                .title("Nivia Football Size 5")
                .slug("nivia-football-size-5")
                .brand("Nivia")
                .category(catMap.get("sports-books"))
                .description("Nivia Shining Star football. Outer leather material, size 5, optimized bounce and flight.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("95066210")
                .build());

        ProductVariant prod_74_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_74)
                .sku("NIVIA-FB-STAR")
                .price(new BigDecimal("799.0"))
                .compareAtPrice(new BigDecimal("1199.0"))
                .stock(60)
                .attributesJson("{\"size\":\"5\",\"color\":\"Yellow-Black\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_74_var_1)
                .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_75 = productRepository.save(Product.builder()
                .title("Fitbit Charge 6 Smartband")
                .slug("fitbit-charge-6-smartband")
                .brand("Fitbit")
                .category(catMap.get("sports-books"))
                .description("Advanced fitness tracker with built-in GPS, active zone minutes, ECG app, and heart rate monitoring.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("85176290")
                .build());

        ProductVariant prod_75_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_75)
                .sku("FITBIT-C6-BK")
                .price(new BigDecimal("13990.0"))
                .compareAtPrice(new BigDecimal("16990.0"))
                .stock(20)
                .attributesJson("{\"color\":\"Obsidian Black\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_75_var_1)
                .imageUrl("https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_76 = productRepository.save(Product.builder()
                .title("Atomic Habits (Paperback)")
                .slug("atomic-habits-paperback")
                .brand("Penguin")
                .category(catMap.get("sports-books"))
                .description("Tiny Changes, Remarkable Results. An easy & proven way to build good habits & break bad ones.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("49011010")
                .build());

        ProductVariant prod_76_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_76)
                .sku("BOOK-ATOMIC-HAB")
                .price(new BigDecimal("399.0"))
                .compareAtPrice(new BigDecimal("799.0"))
                .stock(150)
                .attributesJson("{\"format\":\"Paperback\",\"author\":\"James Clear\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_76_var_1)
                .imageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_77 = productRepository.save(Product.builder()
                .title("Sapiens (Paperback)")
                .slug("sapiens-paperback")
                .brand("Vintage")
                .category(catMap.get("sports-books"))
                .description("A Brief History of Humankind. Dr. Yuval Noah Harari spans the whole of human history from the evolutionary beginnings.")
                .status("ACTIVE")
                .countryOfOrigin("USA")
                .hsnCode("49011010")
                .build());

        ProductVariant prod_77_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_77)
                .sku("BOOK-SAPIENS")
                .price(new BigDecimal("449.0"))
                .compareAtPrice(new BigDecimal("899.0"))
                .stock(120)
                .attributesJson("{\"format\":\"Paperback\",\"author\":\"Yuval Noah Harari\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_77_var_1)
                .imageUrl("https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_78 = productRepository.save(Product.builder()
                .title("Spalding NBA Basketball")
                .slug("spalding-nba-basketball")
                .brand("Spalding")
                .category(catMap.get("sports-books"))
                .description("Outdoor rubber basketball designed with a deep channel layout for superior grip and dribble.")
                .status("ACTIVE")
                .countryOfOrigin("China")
                .hsnCode("95066220")
                .build());

        ProductVariant prod_78_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_78)
                .sku("SPALD-BB-SIZE7")
                .price(new BigDecimal("1299.0"))
                .compareAtPrice(new BigDecimal("1799.0"))
                .stock(30)
                .attributesJson("{\"size\":\"7\",\"material\":\"Rubber\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_78_var_1)
                .imageUrl("https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_79 = productRepository.save(Product.builder()
                .title("Shimano Gear Bicycle")
                .slug("shimano-gear-bicycle")
                .brand("Hero")
                .category(catMap.get("sports-books"))
                .description("Mountain hybrid bicycle. Features front suspension, 21-speed Shimano shifters, and lightweight alloy frame.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("87120010")
                .build());

        ProductVariant prod_79_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_79)
                .sku("HERO-BIKE-21SPD")
                .price(new BigDecimal("14999.0"))
                .compareAtPrice(new BigDecimal("18999.0"))
                .stock(10)
                .attributesJson("{\"wheelSize\":\"27.5 Inches\",\"gears\":\"21 Speed\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_79_var_1)
                .imageUrl("https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_80 = productRepository.save(Product.builder()
                .title("Vector X Yoga Mat 6mm")
                .slug("vector-x-yoga-mat-6mm")
                .brand("Vector X")
                .category(catMap.get("sports-books"))
                .description("Anti-tear, slip-resistant EVA yoga mat. Comes with a carrying strap for portability.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("95069190")
                .build());

        ProductVariant prod_80_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_80)
                .sku("VECTORX-YOGA-6MM")
                .price(new BigDecimal("549.0"))
                .compareAtPrice(new BigDecimal("899.0"))
                .stock(90)
                .attributesJson("{\"thickness\":\"6mm\",\"color\":\"Purple\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_80_var_1)
                .imageUrl("https://images.unsplash.com/photo-1592432678016-e910b452f9a2?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: grocery

        Product prod_81 = productRepository.save(Product.builder()
                .title("Fortune Sunflower Oil 5L")
                .slug("fortune-sunflower-oil-5l")
                .brand("Fortune")
                .category(catMap.get("grocery"))
                .description("Refined sunflower oil. Light, healthy, and easy to digest. High smoke point.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("15121910")
                .build());

        ProductVariant prod_81_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_81)
                .sku("FORT-SUN-5L")
                .price(new BigDecimal("649.0"))
                .compareAtPrice(new BigDecimal("799.0"))
                .stock(200)
                .attributesJson("{\"quantity\":\"5L\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_81_var_1)
                .imageUrl("https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_82 = productRepository.save(Product.builder()
                .title("Aashirvaad Atta 10kg")
                .slug("aashirvaad-atta-10kg")
                .brand("Aashirvaad")
                .category(catMap.get("grocery"))
                .description("Shudh Chakki whole wheat atta. Zero maida content. Perfect for soft rotis.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("11010000")
                .build());

        ProductVariant prod_82_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_82)
                .sku("AASH-ATTA-10KG")
                .price(new BigDecimal("460.0"))
                .compareAtPrice(new BigDecimal("520.0"))
                .stock(150)
                .attributesJson("{\"quantity\":\"10kg\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_82_var_1)
                .imageUrl("https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_83 = productRepository.save(Product.builder()
                .title("Daawat Basmati Rice 5kg")
                .slug("daawat-basmati-rice-5kg")
                .brand("Daawat")
                .category(catMap.get("grocery"))
                .description("Rozana Gold basmati rice. Long grains with beautiful aroma. Perfect for daily biryani or pulao.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("10063020")
                .build());

        ProductVariant prod_83_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_83)
                .sku("DAAWAT-RICE-5KG")
                .price(new BigDecimal("499.0"))
                .compareAtPrice(new BigDecimal("599.0"))
                .stock(120)
                .attributesJson("{\"quantity\":\"5kg\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_83_var_1)
                .imageUrl("https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_84 = productRepository.save(Product.builder()
                .title("Nescafe Classic Coffee")
                .slug("nescafe-classic-coffee")
                .brand("Nescafe")
                .category(catMap.get("grocery"))
                .description("100% pure instant coffee powder. Bold taste and rich aroma in a glass jar.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("21011110")
                .build());

        ProductVariant prod_84_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_84)
                .sku("NES-COFFEE-200G")
                .price(new BigDecimal("380.0"))
                .compareAtPrice(new BigDecimal("450.0"))
                .stock(80)
                .attributesJson("{\"quantity\":\"200g\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_84_var_1)
                .imageUrl("https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_85 = productRepository.save(Product.builder()
                .title("Tata Tea Premium 1kg")
                .slug("tata-tea-premium-1kg")
                .brand("Tata")
                .category(catMap.get("grocery"))
                .description("India's favorite tea blend, offering a strong cup and refreshing rich taste.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("09024020")
                .build());

        ProductVariant prod_85_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_85)
                .sku("TATA-TEA-1KG")
                .price(new BigDecimal("420.0"))
                .compareAtPrice(new BigDecimal("490.0"))
                .stock(100)
                .attributesJson("{\"quantity\":\"1kg\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_85_var_1)
                .imageUrl("https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_86 = productRepository.save(Product.builder()
                .title("Britannia Good Day Biscuits")
                .slug("britannia-good-day-biscuits")
                .brand("Britannia")
                .category(catMap.get("grocery"))
                .description("Cashew butter cookies. Loaded with rich cashews and butter, melted indulgence in every bite.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("19053100")
                .build());

        ProductVariant prod_86_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_86)
                .sku("BRIT-GD-COOKIES")
                .price(new BigDecimal("120.0"))
                .compareAtPrice(new BigDecimal("150.0"))
                .stock(300)
                .attributesJson("{\"quantity\":\"Pack of 8\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_86_var_1)
                .imageUrl("https://images.unsplash.com/photo-1558961309-dbdf71799f14?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_87 = productRepository.save(Product.builder()
                .title("Surf Excel Easy Wash 3kg")
                .slug("surf-excel-easy-wash-3kg")
                .brand("Surf Excel")
                .category(catMap.get("grocery"))
                .description("Detergent powder removing tough stains easily without affecting color or fabric.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("34025000")
                .build());

        ProductVariant prod_87_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_87)
                .sku("SURF-EW-3KG")
                .price(new BigDecimal("410.0"))
                .compareAtPrice(new BigDecimal("499.0"))
                .stock(110)
                .attributesJson("{\"quantity\":\"3kg\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_87_var_1)
                .imageUrl("https://images.unsplash.com/photo-1607344645866-009c320c5ab8?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_88 = productRepository.save(Product.builder()
                .title("Amul Pure Cow Ghee 1L")
                .slug("amul-pure-cow-ghee-1l")
                .brand("Amul")
                .category(catMap.get("grocery"))
                .description("100% pure cow ghee with rich granular texture and aroma. Healthy fat source.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("04059030")
                .build());

        ProductVariant prod_88_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_88)
                .sku("AMUL-GHEE-1L")
                .price(new BigDecimal("660.0"))
                .compareAtPrice(new BigDecimal("720.0"))
                .stock(90)
                .attributesJson("{\"quantity\":\"1L\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_88_var_1)
                .imageUrl("https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_89 = productRepository.save(Product.builder()
                .title("Kellogg's Corn Flakes 1.2kg")
                .slug("kelloggs-corn-flakes-12kg")
                .brand("Kellogg's")
                .category(catMap.get("grocery"))
                .description("High in iron, Vitamin C, and B vitamins. Start your day with a crunchy bowl.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("19041010")
                .build());

        ProductVariant prod_89_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_89)
                .sku("KELL-FLAKES-1.2KG")
                .price(new BigDecimal("380.0"))
                .compareAtPrice(new BigDecimal("450.0"))
                .stock(75)
                .attributesJson("{\"quantity\":\"1.2kg\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_89_var_1)
                .imageUrl("https://images.unsplash.com/photo-1586444248902-2f64eddc13df?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_90 = productRepository.save(Product.builder()
                .title("Dettol Liquid Handwash 1.5L")
                .slug("dettol-liquid-handwash-15l")
                .brand("Dettol")
                .category(catMap.get("grocery"))
                .description("Liquid soap handwash refill pack. Provides 99.9% germ protection.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("34013011")
                .build());

        ProductVariant prod_90_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_90)
                .sku("DETTOL-HW-1.5L")
                .price(new BigDecimal("249.0"))
                .compareAtPrice(new BigDecimal("299.0"))
                .stock(140)
                .attributesJson("{\"quantity\":\"1.5L\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_90_var_1)
                .imageUrl("https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Seeding Category: travel

        Product prod_91 = productRepository.save(Product.builder()
                .title("Mumbai to Delhi Flight Ticket")
                .slug("mumbai-to-delhi-flight-ticket")
                .brand("IndiGo")
                .category(catMap.get("travel"))
                .description("Standard Economy Class one-way flight ticket. Includes 15kg check-in baggage.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99641100")
                .build());

        ProductVariant prod_91_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_91)
                .sku("FLIGHT-BOM-DEL")
                .price(new BigDecimal("5499.0"))
                .compareAtPrice(new BigDecimal("7500.0"))
                .stock(50)
                .attributesJson("{\"class\":\"Economy\",\"type\":\"One-way\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_91_var_1)
                .imageUrl("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_92 = productRepository.save(Product.builder()
                .title("Bangalore to Mumbai Flight")
                .slug("bangalore-to-mumbai-flight")
                .brand("Air India")
                .category(catMap.get("travel"))
                .description("One-way flight ticket. Full service carrier including complimentary hot meal onboard.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99641100")
                .build());

        ProductVariant prod_92_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_92)
                .sku("FLIGHT-BLR-BOM")
                .price(new BigDecimal("4899.0"))
                .compareAtPrice(new BigDecimal("6500.0"))
                .stock(40)
                .attributesJson("{\"class\":\"Economy\",\"type\":\"One-way\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_92_var_1)
                .imageUrl("https://images.unsplash.com/photo-1483450388369-9ed95738483c?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_93 = productRepository.save(Product.builder()
                .title("Taj Palace Delhi Stay")
                .slug("taj-palace-delhi-stay")
                .brand("Taj Hotels")
                .category(catMap.get("travel"))
                .description("Luxury stay for 2 nights in a Superior Room. Includes complimentary breakfast and spa discount.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_93_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_93)
                .sku("HOTEL-TAJ-DEL")
                .price(new BigDecimal("28000.0"))
                .compareAtPrice(new BigDecimal("35000.0"))
                .stock(10)
                .attributesJson("{\"nights\":\"2\",\"occupancy\":\"2 Adults\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_93_var_1)
                .imageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_94 = productRepository.save(Product.builder()
                .title("Goa Beach Resort Package")
                .slug("goa-beach-resort-package")
                .brand("Marriott Goa")
                .category(catMap.get("travel"))
                .description("Ultimate luxury getaway in Goa. 3 Nights in Ocean View Room. Daily breakfast and dinner included.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_94_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_94)
                .sku("PKGR-GOA-MARRIOTT")
                .price(new BigDecimal("45000.0"))
                .compareAtPrice(new BigDecimal("55000.0"))
                .stock(15)
                .attributesJson("{\"nights\":\"3\",\"occupancy\":\"2 Adults\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_94_var_1)
                .imageUrl("https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_95 = productRepository.save(Product.builder()
                .title("Kedarnath Trek Tour Package")
                .slug("kedarnath-trek-tour-package")
                .brand("Himalayan Tours")
                .category(catMap.get("travel"))
                .description("Guided trek to Kedarnath temple from Haridwar. 4 Days/3 Nights package including transit and tents.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99741100")
                .build());

        ProductVariant prod_95_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_95)
                .sku("TOUR-KEDAR-4D")
                .price(new BigDecimal("9999.0"))
                .compareAtPrice(new BigDecimal("13999.0"))
                .stock(25)
                .attributesJson("{\"duration\":\"4 Days\",\"startPoint\":\"Haridwar\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_95_var_1)
                .imageUrl("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_96 = productRepository.save(Product.builder()
                .title("Kerala Backwaters Houseboat")
                .slug("kerala-backwaters-houseboat")
                .brand("Kumarakom Cruises")
                .category(catMap.get("travel"))
                .description("One night premium private houseboat cruise in Kumarakom backwaters. Includes all meals.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_96_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_96)
                .sku("BOAT-KERALA-1N")
                .price(new BigDecimal("14999.0"))
                .compareAtPrice(new BigDecimal("18999.0"))
                .stock(8)
                .attributesJson("{\"duration\":\"1 Night\",\"occupancy\":\"2 Adults\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_96_var_1)
                .imageUrl("https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_97 = productRepository.save(Product.builder()
                .title("London to Paris Flight Ticket")
                .slug("london-to-paris-flight-ticket")
                .brand("EasyJet")
                .category(catMap.get("travel"))
                .description("One-way flight from London Gatwick to Paris Charles de Gaulle. Hand luggage included.")
                .status("ACTIVE")
                .countryOfOrigin("UK")
                .hsnCode("99641100")
                .build());

        ProductVariant prod_97_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_97)
                .sku("FLIGHT-LON-PAR")
                .price(new BigDecimal("3999.0"))
                .compareAtPrice(new BigDecimal("5500.0"))
                .stock(45)
                .attributesJson("{\"class\":\"Economy\",\"type\":\"One-way\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_97_var_1)
                .imageUrl("https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_98 = productRepository.save(Product.builder()
                .title("Maldives Overwater Villa Stay")
                .slug("maldives-overwater-villa-stay")
                .brand("Paradise Resort Maldives")
                .category(catMap.get("travel"))
                .description("Dream honeymoon package. 4 Nights in Overwater Villa with private plunge pool. Half board meal plan.")
                .status("ACTIVE")
                .countryOfOrigin("Maldives")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_98_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_98)
                .sku("PKGR-MALDIVES-4N")
                .price(new BigDecimal("125000.0"))
                .compareAtPrice(new BigDecimal("160000.0"))
                .stock(5)
                .attributesJson("{\"nights\":\"4\",\"type\":\"Villa Stay\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_98_var_1)
                .imageUrl("https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_99 = productRepository.save(Product.builder()
                .title("Heritage Hotel Jaipur Stay")
                .slug("heritage-hotel-jaipur-stay")
                .brand("ITC Rajputana")
                .category(catMap.get("travel"))
                .description("Royal Rajasthani experience. 2 Nights stay in Executive Club Room. Includes breakfast and guided city tour.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_99_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_99)
                .sku("HOTEL-RAJ-JAP")
                .price(new BigDecimal("16999.0"))
                .compareAtPrice(new BigDecimal("22000.0"))
                .stock(12)
                .attributesJson("{\"nights\":\"2\",\"occupancy\":\"2 Adults\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_99_var_1)
                .imageUrl("https://images.unsplash.com/photo-1598977123418-45f04b01f4ac?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        Product prod_100 = productRepository.save(Product.builder()
                .title("Manali Snow Resort Stay")
                .slug("manali-snow-resort-stay")
                .brand("Solang Valley Resort")
                .category(catMap.get("travel"))
                .description("3 Nights cozy stay amidst snow-capped peaks. Complimentary ski coupon and apple cider bottle.")
                .status("ACTIVE")
                .countryOfOrigin("India")
                .hsnCode("99631100")
                .build());

        ProductVariant prod_100_var_1 = variantRepository.save(ProductVariant.builder()
                .product(prod_100)
                .sku("HOTEL-SOLANG-3N")
                .price(new BigDecimal("18999.0"))
                .compareAtPrice(new BigDecimal("24999.0"))
                .stock(10)
                .attributesJson("{\"nights\":\"3\",\"occupancy\":\"2 Adults\"}")
                .build());

        imageRepository.save(ProductImage.builder()
                .variant(prod_100_var_1)
                .imageUrl("https://images.unsplash.com/photo-1502082553048-f009c37129b9?w=600&auto=format&fit=crop&q=80")
                .displayOrder(0)
                .build());

        // Create or find test users
        com.ecommerce.platform.model.User dummyUser1 = userRepository.findByEmail("customer@ecomm.com")
                .orElseGet(() -> userRepository.save(com.ecommerce.platform.model.User.builder()
                        .email("customer@ecomm.com")
                        .password("$2a$10$8.UnVuG9HHgffUDAlk8GPuaeF9VbW165/15gJbNl4q/V5tX3.7sCe") // password
                        .firstName("John")
                        .lastName("Doe")
                        .role(com.ecommerce.platform.model.Role.CUSTOMER)
                        .active(true)
                        .emailVerified(true)
                        .build()));

        com.ecommerce.platform.model.User dummyUser2 = userRepository.findByEmail("bhai123@gmail.com")
                .orElseGet(() -> userRepository.save(com.ecommerce.platform.model.User.builder()
                        .email("bhai123@gmail.com")
                        .password("$2a$10$8.UnVuG9HHgffUDAlk8GPuaeF9VbW165/15gJbNl4q/V5tX3.7sCe")
                        .firstName("bhai")
                        .lastName("ji")
                        .role(com.ecommerce.platform.model.Role.CUSTOMER)
                        .active(true)
                        .emailVerified(true)
                        .build()));

        com.ecommerce.platform.model.User dummyUser3 = userRepository.findByEmail("test1@example.com")
                .orElseGet(() -> userRepository.save(com.ecommerce.platform.model.User.builder()
                        .email("test1@example.com")
                        .password("$2a$10$8.UnVuG9HHgffUDAlk8GPuaeF9VbW165/15gJbNl4q/V5tX3.7sCe")
                        .firstName("Test")
                        .lastName("User")
                        .role(com.ecommerce.platform.model.Role.CUSTOMER)
                        .active(true)
                        .emailVerified(true)
                        .build()));

        // Seed reviews for OPPO K14x 5G
        seedReview(prod_oppo, dummyUser1, 5, "Excellent value for money!", "Superb phone! The battery life is absolutely amazing. 6500mAh easily lasts for 2 days on moderate usage. Camera quality is decent for the price. Highly recommended!");
        seedReview(prod_oppo, dummyUser2, 4, "Good battery and performance", "Good performance and display. MediaTek Dimensity 6300 handles daily tasks without any lag. Charging is fast with 45W SUPERVOOC. Wish the front camera was slightly better.");
        seedReview(prod_oppo, dummyUser3, 5, "Fabulous phone!", "Best budget phone in this range. The Icy Blue color looks very premium in hand. Highly recommended for students and elders.");
        seedReview(prod_oppo, dummyUser1, 2, "Disappointing battery charging", "Charging is extremely slow. Sometimes the screen lags. I am not satisfied with the performance. Returning it.");
        seedReview(prod_oppo, dummyUser2, 3, "Average phone", "Average phone. Good battery, but camera is average and gaming is not smooth. Decent for normal use.");

        // Seed reviews for Apple iPhone 15 Pro
        seedReview(prod_1, dummyUser1, 5, "Mind-blowing phone", "The titanium design feels so light and premium. The A17 Pro chip is extremely fast, and the 120Hz display is buttery smooth. Camera is professional grade!");
        seedReview(prod_1, dummyUser2, 4, "Excellent camera, average battery", "The cameras are outstanding, especially the 5x optical zoom. Performance is stellar. However, battery life is just about average - requires daily charging.");
        seedReview(prod_1, dummyUser3, 5, "Worth the price", "Simply amazing! Upgraded from an iPhone 11 and the difference is massive. Highly recommended if you want the best of Apple.");

        // Seed reviews for Google Pixel 8 Pro
        seedReview(prod_3, dummyUser1, 5, "Best Android experience", "The screen is gorgeous and the camera is simply unbelievable. The AI features like Best Take and Magic Eraser work like magic. Clean software is a delight.");
        seedReview(prod_3, dummyUser3, 4, "Great phone but runs slightly warm", "Outstanding camera and software. The screen is flat and super bright. The only minor issue is it runs slightly warm during heavy gaming.");
    }

    private void seedReview(Product product, com.ecommerce.platform.model.User user, int rating, String title, String comment) {
        com.ecommerce.platform.model.Review review = new com.ecommerce.platform.model.Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setTitle(title);
        review.setComment(comment);
        reviewRepository.save(review);
    }
}
