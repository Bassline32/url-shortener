#!/bin/bash

echo "===== PLATFORM THREADS TEST ====="
time (
  for i in {1..100}; do
    curl -s "http://localhost:8080/api/v1/testLoadThreads/platform-load" &
  done
  wait
)

echo
echo "===== VIRTUAL THREADS TEST ====="
time (
  for i in {1..100}; do
    curl -s "http://localhost:8080/api/v1/testLoadThreads/virtual-load" &
  done
  wait
)
