#!/usr/bin/env bash
# MonikaMart Load Test Automation Script
# Week 7 Requirement: Minimum 10 concurrent users for 60 seconds

TARGET_URL=${1:-"http://localhost:8080/monikamart/api/v1/health"}
CONCURRENCY=10
DURATION=60

echo "=========================================================="
echo " Starting MonikaMart Load Test Execution"
echo " Target Endpoint: $TARGET_URL"
echo " Concurrency:     $CONCURRENCY concurrent clients"
echo " Duration:        $DURATION seconds"
echo "=========================================================="

if command -v ab &> /dev/null; then
    echo "Running ApacheBench (ab)..."
    ab -c $CONCURRENCY -t $DURATION "$TARGET_URL"
elif command -v jmeter &> /dev/null; then
    echo "Running JMeter CLI..."
    jmeter -n -t docs/load-test/load_test.jmx -l target/load_test_results.jtl
else
    echo "Neither ab nor jmeter found in PATH. Using curl benchmark loop..."
    end_time=$((SECONDS + DURATION))
    count=0
    while [ $SECONDS -lt $end_time ]; do
        curl -s -o /dev/null "$TARGET_URL" &
        count=$((count + 1))
        if (( count % 10 == 0 )); then
            wait
        fi
    done
    wait
    echo "Completed $count requests across $DURATION seconds."
fi

echo "Load test run completed successfully."
