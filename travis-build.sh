#!/usr/bin/env bash

set -e

rm -rf build

./gradlew -q clean check install --stacktrace

 ./integration-test-app/run_integration_tests.sh

echo "branch: $TRAVIS_BRANCH"
echo "pullrequest: $TRAVIS_PULL_REQUEST"
echo "travis tag: $TRAVIS_TAG"

# if [[ -n $TRAVIS_TAG ]] && [[ $TRAVIS_PULL_REQUEST == 'false' ]]; then
    
    # ./gradlew bintrayUpload --stacktrace

    ./publish-docs.sh

# fi
