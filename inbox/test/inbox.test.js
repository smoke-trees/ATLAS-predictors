require('events').EventEmitter.defaultMaxListeners = 0;
const assert = require('assert');
const ganache = require('ganache-cli');
const Web3 = require('web3');
const provider = ganache.provider();
const web3 = new Web3(provider);
const { interface: interfaceContract, bytecode } = require('../compile.js');


let accounts;
let eventData;

beforeEach(async () => {
    // Fetch all the accounts
    accounts = await web3.eth.getAccounts();
    // Use one of them to deploy a contract
    eventData = await new web3.eth.Contract(JSON.parse(interfaceContract))
        .deploy({ data: bytecode, arguments:[] })
        .send({ from: accounts[0], gas: '3000000'});
    console.log(await web3.eth.getBalance(accounts[0]))
});

describe('Election', () => {
    it('deploys a contract', () => {
        assert.ok(eventData.options.address);
    });

    it('should add device data',async () => {
        const deviceId = 432;
        const accessHash = 'df6gs90dg7hd56sd5fd87vm9000f5haj9';
        await eventData.methods.addDeviceData(deviceId, accessHash).send({from:accounts[0]});
        //console.log(await eventData.methods.getDeviceTimestamps(deviceId).call());
    });

    it('should add default candidates in the constructor', async () => {
        const sendId = 2;
        const candidate = await eventData.methods.candidates(sendId).call();
        assert.strictEqual(sendId.toString(), candidate.id);
    });

    it('should be able to vote', async () => {
        await eventData.methods.vote(2).send({from: accounts[0]});
        const candidate = await eventData.methods.candidates(2).call();
        assert.strictEqual(candidate.voteCount, '1');
    });

    it('should not be able to vote twice', async () => {
        await eventData.methods.vote(1).send({from: accounts[0]});
        try {
            await eventData.methods.vote(1).send({from: accounts[0]});
        } catch (e) {
            if (e.toString().includes('revert')) {
                assert(true);
            } else {
                assert(false);
            }
        }

    });

});