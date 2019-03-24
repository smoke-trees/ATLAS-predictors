const app = require('./app');

appPort = process.env.PORT || 3000;

const server = app.listen(appPort, () => {
    console.log(`Express is running on port ${server.address().port}`);
});