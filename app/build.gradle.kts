plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("jacoco")
}

sonar {
    properties {
        property("sonar.host.url", "http://localhost:9000/")
        property("sonar.projectKey", "")
        property("sonar.projectName", "")
        property("sonar.token", "")
        property("sonar.tests", "src/test/java")
        property("sonar.test.inclusions", "**/*Test*/**")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "src/main/java")
        property("sonar.exclusions", listOf(
            "**/*Test*/**",
            "build/**",
            "*.json",
            "**/*test*/**",
            "**/.gradle/**",
            "**/R.class"
        ).joinToString(", "))
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths", "**/test-results/**/*.xml")
        property("sonar.coverage.jacoco.xmlReportPaths",
            "${projectDir}/build/reports/jacoco/jacocoFullReport/jacocoFullReport.xml")
    }
}

// List of modules that don't require Jacoco
val ignoredByJacoco = mutableListOf<String>()
val fileFilter = mutableListOf<String>()
val buildKotlinClasses = "/tmp/kotlin-classes/debug"
val buildJavaClasses = "/intermediates/javac/debug/classes"
val testExecutionFile =
    "/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"

// List of files that can be ignored for test coverage
val coverageExclusions = listOf(
    "**/R.class",
    "**/R$*.class"
)

// Apply additional build steps to sub-projects
subprojects.forEach() { project ->
    if (!ignoredByJacoco.contains(project.name)) {
        project.pluginManager.apply("jacoco")

        project.extensions.configure(org.gradle.testing.jacoco.plugins.JacocoPluginExtension::class.java) {
            toolVersion = "0.8.7"
        }

        project.tasks.register("jacocoReport", JacocoReport::class.java) {
            group = "Reporting"
            description = "Generate Jacoco coverage reports after running tests."
            dependsOn("testDebugUnitTest")

            val buildDebug = listOf("${project.buildDir}$buildKotlinClasses",
                "${project.buildDir}$buildJavaClasses")
            val classDirs = project.files(buildDebug.map { classPath ->
                project.fileTree(classPath) {
                    // include(fileFilter)
                    exclude(coverageExclusions as List<String>)
                }
            })

            val mainSrc = "${project.projectDir}/src/main/java"
            sourceDirectories.from(project.files(mainSrc))
            classDirectories.from(classDirs)
            executionData.from(project.files("${project.buildDir}$testExecutionFile"))

            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
    }
}

// Root task that generates an aggregated Jacoco test coverage report for all subprojects
tasks.register("jacocoFullReport", JacocoReport::class.java) {
    group = "Reporting"
    description = "Generates an aggregate report from all subprojects"

    // Initialize an empty ConfigurableFileCollection
    val classDirs = project.files()

    // Filter subprojects to include only those not ignored
    val includedProjects = subprojects.filter { !ignoredByJacoco.contains(it.name) }

    // Collect class directories from included projects
    includedProjects.forEach { prj ->
        val buildDebug = listOf(
            "${prj.buildDir}$buildKotlinClasses",
            "${prj.buildDir}$buildJavaClasses"
        )

        buildDebug.forEach { classPath ->
            val fileTree = prj.fileTree(classPath) {
                // include(fileFilter)
                exclude(coverageExclusions as List<String>)
            }

            // Accumulate file trees into the classDirs collection
            classDirs.from(fileTree)
        }
    }

    val dirs = includedProjects.map { "${it.projectDir}/src/main/java" }
    sourceDirectories.setFrom(project.files(dirs))
    classDirectories.setFrom(classDirs)
    executionData.setFrom(includedProjects.flatMap {
        it.tasks.withType(JacocoReport::class.java).map { task -> task.executionData } })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

android {
    namespace = "com.example.weclean"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weclean"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.maps.android:android-maps-utils:3.8.2")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("junit:junit:4.13.2")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.testng:testng:6.9.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("org.mockito:mockito-core:3.12.4")
    implementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    implementation ("org.powermock:powermock-module-junit4:2.0.9")
    implementation ("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation ("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation ("org.powermock:powermock-api-mockito2:2.0.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

