import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    androidLibrary {
       namespace = "ph.com.alexcr.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
//    val webMain by creating {
//        dependsOn(commonMain.get())
//    }
//
//    jsMain {
//        dependsOn(webMain)
//    }
//
//    wasmJsMain {
//        dependsOn(webMain)
//    }

    sourceSets {
        webMain.dependencies {
            implementation(libs.sqldelight.web.worker.driver)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
//            implementation(libs.navigation3.runtime)
            implementation(libs.navigation3.ui)

            implementation(libs.material.icons.extended)
            implementation(libs.androidx.ui.text.google.fonts)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.core)
            implementation(libs.bundles.ktor)

            implementation(libs.sqldelight.coroutines.extensions)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.ios.driver)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqldelight.get()))
            implementation(npm("sql.js", "1.12.0"))
            implementation(devNpm("copy-webpack-plugin", "12.0.2"))
        }
    }
}

sqldelight {
    databases {
        create("BudgetTrackerDatabase") {
            packageName.set("ph.com.alexcr.tracker.database")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}