plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.nsu.fit.sckwo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
//    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.darkrockstudios:mpfilepicker:3.1.0")
    implementation("com.godaddy.android.colorpicker:compose-color-picker:0.7.0")
    implementation("com.google.code.gson:gson:2.8.8")
//    implementation("com.github.skydoves:colorpicker-compose:1.0.8")
//    implementation("com.github.skydoves:colorpicker-compose:1.0.8-SNAPSHOT")
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}