plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
//    id("com.google.protobuf")
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:3.22.3"
//    }
//    generateProtoTasks {
//        ofSourceSet("main").forEach { task ->
//            task.plugins {
//                kotlin { }
//            }
//        }
//    }
//}

android {

    namespace = "surcharge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.surcharge"
        minSdk = 30
        targetSdk = 34
        versionCode = 41
        versionName = "1.40"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

//    configurations.all {
//        resolutionStrategy {
//            force("com.google.protobuf:protobuf-javalite:3.22.3")
//        }
//    }
//
//    sourceSets {
//        getByName("main") {
//            kotlin {
//                srcDir("src/main/kotlin")
//            }
//            proto {
//                srcDir("src/main/proto")
//            }
//        }
//    }
}

dependencies {

//    protobuf("com.google.protobuf:protobuf-javalite:3.22.3")
//    implementation("com.google.firebase:protolite-well-known-types:18.0.0")
//    implementation("com.google.protobuf:protobuf-java:4.31.1")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.3.2")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.appcompat:appcompat:1.7.1")

    // auth
    implementation("com.google.android.gms:play-services-auth:21.3.0")
//    implementation("androidx.credentials:credentials:1.5.0-alpha02")
//    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-alpha02")
//    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.firebaseui:firebase-ui-auth:9.0.0")

    //  images
    implementation("io.coil-kt:coil-compose:2.7.0")

    // cloudinary images
    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // env
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // square reader
//    implementation("com.squareup.sdk:mobile-payments-sdk:2.3.0") {
    implementation("com.squareup.sdk:mobile-payments-sdk:2.0.0-beta2") {
        exclude("com.google.protobuf", "protobuf-java")
    }
//    implementation("com.squareup.sdk:mockreader-ui:2.0.0-beta1")

    // local database - Room
    implementation("androidx.room:room-runtime:2.7.2")
    annotationProcessor("androidx.room:room-compiler:2.7.1")

    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:2.7.2")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.7.1")

    // optional - Test helpers
//    testImplementation("androidx.room:room-testing:2.6.1")

    // gson conversion
    implementation("com.google.code.gson:gson:2.13.1")

    // datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // graphs
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.19")

    // https
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-firestore") {
//        exclude("com.google.protobuf", "protobuf-java")
//        exclude("com.google.protobuf", "protobuf-javalite")
//        exclude("com.google.firebase", "protolite-well-known-types")
    }

    implementation("com.google.firebase:firebase-auth") {
//        exclude("com.google.protobuf", "protobuf-java")
//        exclude("com.google.protobuf", "protobuf-javalite")
//        exclude("com.google.firebase", "protolite-well-known-types")
    }
//    implementation("com.google.firebase:firebase-analytics")
}