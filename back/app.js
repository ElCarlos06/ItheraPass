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

// Endpoint de prueba (debe ir ANTES de las rutas generales)
app.get('/test', (req, res) => {
    res.json({ 
        success: true, 
        message: 'Servidor funcionando correctamente',
        timestamp: new Date().toISOString(),
        database: 'MySQL'
    });
});

app.get('/', (req, res) => {
    res.send('funciona');
});

// Rutas de la API (después de las rutas específicas)
app.use('/', fila);
app.use('/', turno);

app.listen(port, '0.0.0.0', () => {
    console.log('Servido conectado en el puerto '+port);
});
