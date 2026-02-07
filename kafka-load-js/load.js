const { Kafka } = require('kafkajs');
const crypto = require('crypto');

const kafka = new Kafka({
    clientId: 'load-generator',
    brokers: ['localhost:19092']
});

const producer = kafka.producer();

const categories = ["test", "payment", "transfer"];

function randomCategory() {
    return categories[Math.floor(Math.random() * categories.length)];
}


const ALMATY_UTC_OFFSET_HOURS = 5;

function getNextWorkingDayUTC(dateUtc) {

    const day = dateUtc.getUTCDay(); // 0 вс, 6 сб
    if (day === 6) dateUtc.setUTCDate(dateUtc.getUTCDate() + 2); // сб -> пн
    if (day === 0) dateUtc.setUTCDate(dateUtc.getUTCDate() + 1); // вс -> пн
    return dateUtc;
}

function getTodayYMDInAlmaty() {
    const formatter = new Intl.DateTimeFormat('en-CA', {
        timeZone: 'Asia/Almaty',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });

    const parts = formatter.formatToParts(new Date());
    const year = Number(parts.find(p => p.type === 'year').value);
    const month = Number(parts.find(p => p.type === 'month').value);
    const day = Number(parts.find(p => p.type === 'day').value);

    return { year, month, day };
}

function randomInt(min, max) {

    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function createBiasedTimestampAlmaty() {
    const WORK_HOURS_PROB = 0.75;

    const { year, month, day } = getTodayYMDInAlmaty();


    const baseUtc = new Date(Date.UTC(year, month - 1, day, 12 - ALMATY_UTC_OFFSET_HOURS, 0, 0));
    const workdayUtc = getNextWorkingDayUTC(baseUtc);


    let hourAlmaty;
    if (Math.random() < WORK_HOURS_PROB) {

        hourAlmaty = randomInt(9, 18);
    } else {

        const isNight = Math.random() < 0.5;
        hourAlmaty = isNight ? randomInt(0, 8) : randomInt(19, 23);
    }

    const minute = randomInt(0, 59);
    const second = randomInt(0, 59);


    const y = workdayUtc.getUTCFullYear();
    const m = workdayUtc.getUTCMonth();
    const d = workdayUtc.getUTCDate();

    const createdAtUtc = new Date(Date.UTC(
        y, m, d,
        hourAlmaty - ALMATY_UTC_OFFSET_HOURS,
        minute,
        second
    ));

    return createdAtUtc.toISOString();
}

function createEvent() {
    return {
        eventId: crypto.randomUUID(),
        clientId: crypto.randomUUID(),
        caseId: crypto.randomUUID(),
        category: randomCategory(),
        createdAt: createBiasedTimestampAlmaty()
    };
}

async function runLoad(totalMessages = 10000) {
    await producer.connect();
    console.log("Started load generation...");

    for (let i = 0; i < totalMessages; i++) {
        const event = createEvent();

        await producer.send({
            topic: 'topic-1',
            messages: [{ value: JSON.stringify(event) }]
        });

        await new Promise(resolve => setTimeout(resolve, 10));
    }

    console.log("Load finished");
    await producer.disconnect();
}

runLoad();
