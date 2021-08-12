#!/usr/bin/env bash

helm upgrade --install balcony-api -n dandelion-mn-v9 -f deploy/balcony-api/values-mainnet.yaml deploy/balcony-api
