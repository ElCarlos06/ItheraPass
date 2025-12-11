/*nbhb
var
let
const
*/

const express = require('express');
const cors = require('cors');

const fila = require('./routes/fila');
const turno = require('./routes/turno');

const app = express();
const port = 3000;

app.use(cors());        
app.use(express.json());  


app.use('/', fila);

app.get('/', (req, res) => {
    res.send('funciona');
});

app.listen(port, '0.0.0.0', () => {
    console.log('Servido conectado en el puerto '+port);
});