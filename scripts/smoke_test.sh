#!/usr/bin/env bash
# MonikaMart Bash Automated Smoke Test
BASE_URL=${1:-"http://localhost:8080/monikamart"}

echo "=========================================================="
echo " MonikaMart Smoke Test Execution (Bash)"
echo " Base URL: $BASE_URL"
echo "=========================================================="

test_url() {
    local name="$1"
    local url="$2"
    local expected="$3"
    local method="${4:-GET}"
    local data="${5:-}"

    if [ "$method" = "POST" ]; then
        status=$(curl -s -o /dev/null -w "%{http_code}" -X POST -H "Content-Type: application/json" -d "$data" "$url")
    else
        status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    fi

    if [ "$status" -eq "$expected" ]; then
        echo -e "\e[32m[PASS]\e[0m $name (HTTP $status)"
    else
        echo -e "\e[31m[FAIL]\e[0m $name (Expected $expected, got $status)"
        return 1
    fi
}

test_url "GET /api/v1/health" "$BASE_URL/api/v1/health" 200 || exit 1
test_url "GET /products" "$BASE_URL/products" 200 || exit 1
test_url "GET /login" "$BASE_URL/login" 200 || exit 1
test_url "POST /api/chat" "$BASE_URL/api/chat" 200 "POST" '{"message":"Return policy"}' || exit 1

echo "=========================================================="
echo -e "\e[32mAll smoke tests passed!\e[0m"
