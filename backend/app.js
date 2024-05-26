import sqlite3 from 'sqlite3'	

const cors = require('cors')
const bycrypt = require('bcrypt')
const express  =require('express')
const app = express()

app.use(cors())
app.use(express.json())

let db = new sqlite3.Database('./users.db', (err) => {
    if (err) {
        console.log(err)
    } else {
        console.log("Connected to database")
    }
})
db.run('CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT, phone TEXT)');


app.post('/register', (req, res) => {
    const {name, email, password, phone} = req.body;
    const hashedPassword = bycrypt.hashSync(password, 10);

    const stmt = db.prepare('INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)');
    stmt.run(name, email, hashedPassword, phone, function(err) {
        if (err) {
            console.log(err)
        } else {
            res.json({message: 'Registration successful'})
        }
    
    });

    stmt.finalize()


})


app.post('/login', (req, res) => {
    const {email, password} = req.body;

    db.get('SELECT * FROM users WHERE email = ?', [email], (err, row) => {
        if (err) {
            console.log(err)
        } else {
            if (row) {
                const isPasswordCorrect = bycrypt.compareSync(password, row.password);
                if (isPasswordCorrect) {
                    res.json({message: 'Login successful'})
                } else {
                    res.status(401).json({message: 'Invalid password'})
                }
            } else {
                res.status(401).json({message: 'User not found'})
            }
        }
    })
})



app.listen(3000, () => {
    console.log("listening on 3000")
})
