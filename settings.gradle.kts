includeBuild("includedBuild/dependencies")
includeBuild("includedBuild/project-settings")
includeBuild("includedBuild/publish")

include(
        ":app",
        "core",
        "firebase-core",
        ":firebase-facebook",
        ":firebase-google",
        ":google-core",
        ":facebook-core",
        ":google",
        ":facebook"
)
