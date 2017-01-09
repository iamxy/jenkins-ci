#!groovy

node {
    def workspace = pwd()
    def globalTestValue = "test123"
    def integrationTest
    def getChangeLogText
    def getBuildDuration

    catchError {
        stage('Checkout') {
            dir("bank-test"){
                git url: 'https://github.com/iamxy/jenkins-ci.git'
            }
            
            // common
            fileLoader.withGit('https://github.com/iamxy/jenkins-ci.git', 'master', null, '') {
                getChangeLogText = fileLoader.load('get_changelog_text.groovy')
                getBuildDuration = fileLoader.load('get_build_duration.groovy')
                integrationTest = fileLoader.load('integration_test_snippet.groovy')
            }

            stash includes: 'bank-test/**', name: 'source-pingcap'
        }

        stage('Test') {
            def branches = [:]

            integrationTest(branches)

            parallel branches
        }

        currentBuild.result = "SUCCESS"
    }

    def buildDuration = getBuildDuration()
    def changeLogText = getChangeLogText()

    echo "buildDuration: ${buildDuration}"
    echo "changeLogText: ${changeLogText}"
}
