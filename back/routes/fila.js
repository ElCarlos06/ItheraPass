const { 
    getFilas, 
    getFila, 
    createFila, 
    deleteFila, 
} = require('../controllers/fila.controller');

const express = require('express');

const router = express.Router();

router.get('/fila', getFilas);
router.get('/fila/:id', getFila);
router.post('/fila', createFila);
router.delete('/fila/:id', deleteFila);

module.exports = router;