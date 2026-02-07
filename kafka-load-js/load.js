const { Kafka } = require('kafkajs');

const kafka = new Kafka({
    clientId: 'load-generator',
    brokers: ['localhost:29092']
});

const producer = kafka.producer();

const categories = [
    "test",
    "payment",
    "transfer"
];

function randomCategory() {
    return categories[Math.floor(Math.random() * categories.length)];
}

function createEvent() {
    return {
        clientId: crypto.randomUUID(),
        eventId: crypto.randomUUID(),
        category: randomCategory(),
        createdAt: new Date().toISOString()
    };
}

async function runLoad(totalMessages = 10000) {

    await producer.connect();

    console.log("Started load generation...");

    for (let i = 0; i < totalMessages; i++) {

        const event = createEvent();

        await producer.send({
            topic: 'topic-1',
            messages: [
                { value: JSON.stringify(event) }
            ]
        });

        await new Promise(resolve => setTimeout(resolve, 10));
    }

    console.log("Load finished");
    await producer.disconnect();
}

runLoad();
