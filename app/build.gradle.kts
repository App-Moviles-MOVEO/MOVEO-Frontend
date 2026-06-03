import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY", "")
val baseUrl: String = localProperties.getProperty("BASE_URL", "http://10.0.2.2:8080/")
// Stripe: la publishable key va en la app; la secret key SOLO se usa en modo demo sin backend.
val stripePublishableKey: String = localProperties.getProperty("STRIPE_PUBLISHABLE_KEY", "")
val stripeSecretKey: String = localProperties.getProperty("STRIPE_SECRET_KEY", "")
val stripeCurrency: String = localProperties.getProperty("STRIPE_CURRENCY", "pen")
// Bandera global (back-compat). Si vale true, fuerza mock en todos los modulos sin importar las banderas individuales.
val useMockData: Boolean = localProperties.getProperty("USE_MOCK_DATA", "true").toBooleanStrict()
// Banderas por modulo: permiten conectar al backend feature por feature.
// Cuando USE_MOCK_DATA=false, cada modulo respeta su propia bandera (default true = mock).
val useMockAuth: Boolean = localProperties.getProperty("USE_MOCK_AUTH", "true").toBooleanStrict()
val useMockRental: Boolean = localProperties.getProperty("USE_MOCK_RENTAL", "true").toBooleanStrict()
val useMockCarpooling: Boolean = localProperties.getProperty("USE_MOCK_CARPOOLING", "true").toBooleanStrict()
val useMockBilling: Boolean = localProperties.getProperty("USE_MOCK_BILLING", "true").toBooleanStrict()
val useMockOperations: Boolean = localProperties.getProperty("USE_MOCK_OPERATIONS", "true").toBooleanStrict()

android {
    namespace = "com.example.moveo_frontend"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.moveo_frontend"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"$stripePublishableKey\"")
        buildConfigField("String", "STRIPE_SECRET_KEY", "\"$stripeSecretKey\"")
        buildConfigField("String", "STRIPE_CURRENCY", "\"$stripeCurrency\"")
        buildConfigField("Boolean", "USE_MOCK_DATA", "$useMockData")
        buildConfigField("Boolean", "USE_MOCK_AUTH", "$useMockAuth")
        buildConfigField("Boolean", "USE_MOCK_RENTAL", "$useMockRental")
        buildConfigField("Boolean", "USE_MOCK_CARPOOLING", "$useMockCarpooling")
        buildConfigField("Boolean", "USE_MOCK_BILLING", "$useMockBilling")
        buildConfigField("Boolean", "USE_MOCK_OPERATIONS", "$useMockOperations")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)

    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

    implementation(libs.stripe.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
