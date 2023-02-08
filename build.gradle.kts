buildscript {
    extra.apply {
        set("compose_ui_version", "1.3.3")
    }
}
plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.0" apply false
}