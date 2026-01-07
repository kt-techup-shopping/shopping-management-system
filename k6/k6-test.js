import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE = 'http://host.docker.internal:8080/api/v1/products';
const TOTAL_PAGES = 5000;
const SIZE = 10;

function randInt(min, maxInclusive) {
    return Math.floor(Math.random() * (maxInclusive - min + 1)) + min;
}

export const options = {
    scenarios: {
        users_100: {
            executor: 'constant-vus',
            vus: 100,
            duration: '100s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

export default function () {
    const page = randInt(1, TOTAL_PAGES);
    const url = `${BASE}?page=${page}&size=${SIZE}`;

    const res = http.get(url, { tags: { name: 'products_random_page' } });
    check(res, {
        'ok': (r) => r.status === 200,
    });
    sleep(1);
}
