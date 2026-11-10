#!/bin/bash

set -e

cd frontend

npm install
npm run build

rm -rf ../src/main/resources/static/*
cp -r dist/* ../src/main/resources/static/

echo "Done."