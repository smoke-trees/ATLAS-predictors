const HDWalletProvider = require('truffle-hdwallet-provider');
const Web3 = require('web3');
const ETH_MNEMONIC = 'cruel soft deal wolf dance spoil steak used short fatal husband fashion';
const NETWORK_ENDPOINT = 'https://rinkeby.infura.io/v3/c50f0a9cc37642b4a6da71c669855a1a';
const provider = new HDWalletProvider(ETH_MNEMONIC, NETWORK_ENDPOINT);


const web3 = new Web3(provider);

module.exports = web3;