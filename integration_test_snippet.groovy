def call(branches) {
    branches["Unit Test"] = {
        echo "workspace: ${globalTestValue}"
            dir("output") {
                unstash "source-pingcap"
            }
    }
}

return this

