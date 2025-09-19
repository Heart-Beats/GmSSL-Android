apply(from = "./commonGradle/gradle_config_optimize.gradle")

rootProject.name = "GmSSL-Android"
include(":app")


include(":gmssl-android")
project(":gmssl-android").projectDir = file("./GmSSL3")