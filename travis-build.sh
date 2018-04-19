#!/usr/bin/env bash

set -e

rm -rf build

./gradlew -q clean check install --stacktrace

EXIT_STATUS=0

echo "Running Integration tests ... "
./integration-test-app/run_integration_tests.sh

echo "branch: $TRAVIS_BRANCH"
echo "pull-request: $TRAVIS_PULL_REQUEST"
echo "travis tag: $TRAVIS_TAG"

if [[ -n $TRAVIS_TAG ]] || [[ $TRAVIS_BRANCH == 'master' && $TRAVIS_PULL_REQUEST == 'false' ]]; then

    echo "Publishing archives ... "

    if [[ -n $TRAVIS_TAG ]]; then
      ./gradlew bintrayUpload || EXIT_STATUS=$?
    else
      ./gradlew publish || EXIT_STATUS=$?
    fi

    ./publish-docs.sh

fi
