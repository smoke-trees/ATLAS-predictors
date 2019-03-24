const HDWalletProvider = require('truffle-hdwallet-provider');
const Web3 = require('web3');

const ETH_MNEMONIC = 'cruel soft deal wolf dance spoil steak used short fatal husband fashion';
const NETWORK_ENDPOINT = 'https://rinkeby.infura.io/v3/c50f0a9cc37642b4a6da71c669855a1a';

const provider = new HDWalletProvider(ETH_MNEMONIC, NETWORK_ENDPOINT);
const web3 = new Web3(provider);

const deploy = async () => {
    const accounts = await web3.eth.getAccounts();

    console.log('Attempting to deploy from account', accounts[0]);
    const result = await new web3.eth.Contract(JSON.parse(interfaceContract))
        .deploy({data: '0x' + bytecode, arguments: []})
        .send({gas: '1000000', from: accounts[0]});
    console.log(interfaceContract);
    console.log('Contract deployed to', result.options.address);
};

//deploy();

const address = '0x6CACabdf0778cD35080F97a7b7616848CD89E507';
const abi = '[{"constant":true,"inputs":[],"name":"get_device_count","outputs":[{"name":"count","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[],"name":"vendor","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[],"name":"kill","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"device_id","type":"address"}],"name":"get_device_timestamps","outputs":[{"name":"timestamp","type":"uint256[]"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"device_id","type":"address"},{"name":"filehash","type":"string"}],"name":"set_device_data","outputs":[{"name":"index","type":"uint256"},{"name":"timestamp","type":"uint256"}],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"device_id","type":"address"}],"name":"is_device_present","outputs":[{"name":"result","type":"bool"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":true,"inputs":[{"name":"device_id","type":"address"},{"name":"timestamp","type":"uint256"}],"name":"get_device_data","outputs":[{"name":"hash","type":"string"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":true,"inputs":[{"name":"index","type":"uint256"}],"name":"get_device_at_index","outputs":[{"name":"device_address","type":"address"}],"payable":false,"stateMutability":"view","type":"function"},{"anonymous":false,"inputs":[{"indexed":true,"name":"device_id","type":"address"},{"indexed":false,"name":"index","type":"uint256"},{"indexed":false,"name":"timestamp","type":"uint256"},{"indexed":false,"name":"filehash","type":"string"}],"name":"log_action","type":"event"}]';
const eventData = new web3.eth.Contract(JSON.parse(abi), address);
let accounts;

test = async () => {
    accounts = await web3.eth.getAccounts();
    console.log(await eventData.methods.set_device_data(accounts[0], 'test data').send({from: accounts[0]}));
};

test();

module.exports = eventData;
