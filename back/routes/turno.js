const express = require('express');
const router = express.Router();

const { 
    createTurno, 
    getTurnos, 
    updateTurno, 
    getMisFilas 
} = require('../controllers/turno.controller');

router.post('/turno', createTurno);              
router.get('/fila/:id/turnos', getTurnos);       
router.put('/turno/:id', updateTurno);           
router.get('/mis-filas/:idUsuario', getMisFilas);

module.exports = router;