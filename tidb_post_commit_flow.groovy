#!groovy

node {
    def workspace = pwd()

    catchError {
        stage('Checkout') {
            dir("bank-test"){
                git url: 'https://github.com/iamxy/jenkins-ci.git'
            }

            stash includes: 'bank-test/**', name: 'source-pingcap'
        }

        stage('Test') {
            def branches = [:]

            def integrationTest = load('integration_test_snippet.groovy')
            integrationTest(branches)

            parallel branches
        }

        currentBuild.result = "SUCCESS"
    }

    def changeLogText = ""
    for (int i = 0; i < currentBuild.changeSets.size(); i++) {
        for (int j = 0; j < currentBuild.changeSets[i].items.length; j++) {
            def commitId = "${currentBuild.changeSets[i].items[j].commitId}"
            def commitMsg = "${currentBuild.changeSets[i].items[j].msg}"
            changeLogText += "\n" + commitId.take(7) + " " + commitMsg
        }
    }

    echo "${changeLogText}"
}
