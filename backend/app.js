const questions = require('./questions.json')
const sentencesJs = require('./sentences.json')
const sqlite3 = require('sqlite3').verbose();
const cors = require('cors')
const bcrypt = require('bcrypt')
const express = require('express')
const app = express()
const nano = require('nano')
const jwt = require('jsonwebtoken')
require('dotenv').config()
// import questions from './questions.json'

const JWT_SECRET = process.env.JWT_SECRET


app.use(cors())
app.use(express.json())

let db = new sqlite3.Database('./users.db', (err) => {
    if (err) {
        console.log(err)
    } else {
        console.log("Connected to database")
    }
})
db.run('CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT, phone TEXT, image TEXT)');


let refreshToken = [];

app.post('/token', (req, res) => {
    const refreshToken = req.body.token;
    if (!refreshToken) return res.sendStatus(401);
    if (!refreshToken.includes(refreshToken)) return res.sendStatus(403);
    jwt.verify(refreshToken, JWT_SECRET, (err, user) => {
        if (err) return res.sendStatus(403);
        const token = jwt.sign({ email: user.email }, JWT_SECRET, { expiresIn: '1h' });
        res.json({ token })
    })
});

app.post('/register', (req, res) => {
    const { name, email, password, phone } = req.body;
    const hashedPassword = bcrypt.hashSync(password, 10);

    //verify if user already exists

    db.get('SELECT * FROM users WHERE email = ?', [email], (err, row) => {
        if (err) {
            console.log(err)
        } else {
            if (row) {
                res.status(400).json({ message: 'User already exists' })
            } else {
                const stmt = db.prepare('INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)');
                stmt.run(name, email, hashedPassword, phone, function (err) {
                    if (err) {
                        console.error("Error during registration: ", err.message)
                    } else {
                        const token = jwt.sign({ email: email }, JWT_SECRET, {expiresIn: '1h'});
                        res.json({ message: 'Registration successful',token })
                    }

                });

                stmt.finalize()
            }
        }
    })



})

app.get('/users', (req, res) => {
    db.all('SELECT * FROM users', (err, rows) => {
        if (err) {
            console.log(err)
        } else {
            res.json(rows)
        }
    })
})

app.post('/login', (req, res) => {
    const { email, password } = req.body;

    db.get('SELECT * FROM users WHERE email = ?', [email], (err, row) => {
        if (err) {
            console.log(err)
        } else {
            if (row) {
                const isPasswordCorrect = bcrypt.compareSync(password, row.password);
                if (isPasswordCorrect) {
                    const token = jwt.sign({email: row.email }, JWT_SECRET, {expiresIn: '1h'});
                    // res.json({ message: 'Login successful', user: {name: row.name, email: row.email, phone: row.phone, image: row.image} })

                    res.json({ message: 'Login successful', token: token, refreshToken : refreshToken, user: {name: row.name, email: row.email, phone: row.phone, image: row.image} })
                } else {
                    res.status(401).json({ message: 'Invalid password' })
                }
            } else {
                res.status(401).json({ message: 'User not found' })
            }
        }
    })
})

function authenticateToken(req, res, next) {
    // const token = jwt.sign({ user: user.email}, JWT_SECRET);
    const authHeader = req.headers['authorization']
    const token = authHeader && authHeader.split(' ')[1]
    if (!token) {

        console.log("No token provided")
        return res.sendStatus(403)

    }

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            console.log(err)
            return res.sendStatus(403)
        }
            
        req.user = user;
        next();
    });
}
// app.put('/profile', (req, res) => {
app.put('/profile', authenticateToken, (req, res) => {
    const { email, name, phone } = req.body;

    db.run('UPDATE users SET name = ?, phone = ? WHERE email = ?', [name, phone, email], (err) => {
        if (err) {
            console.log(err)
        } else {
            res.json({ message: 'Profile updated' })
        }
    })
})

// app.put('/image',(req, res) => {
app.put('/image', authenticateToken,(req, res) => {
    const { email, image } = req.body;

    db.run('UPDATE users SET image = ? WHERE email = ?', [image, email], (err) => {
        if (err) {
            console.log(err)
        } else {
            res.json({ message: 'Profile updated' })
        }
    })
})

const fetch_ = require('node-fetch')

var nano_url = process.env.NANO_URL
// console.log(nano_url)

const couch = nano({
    url: nano_url,
})
const dbname = 'questions_db'
const questions_db = couch.use(dbname)
couch.db.create(dbname, (err, body) => {
    if (err) {
        console.error(err, body)
    } else {
        console.log("Questions Database created")
    }

    questions.languageLearner.forEach(languageLearner => {
        questions_db.find({
            "selector": {
                "categoryName": languageLearner.categoryName
            }
        }, (err, resp) => {
            if (resp.docs.length > 0) {
                console.log("Question already exists")
                questions_db.insert(languageLearner, {
                    "docName": resp.docs[0]._id,
                    "rev": resp.docs[0]._rev
                }, (err, body) => {
                    if (err) {
                        console.log("error inserting data", err)
                    } else {
                        console.log("Questions inserted", body)
                    }
                })
                return
            }
            questions_db.insert(languageLearner, (err, body) => {
                if (err) {
                    console.log("error inserting data", err)
                } else {
                    console.log("Questions inserted", body)
                }
            })
        })
    })
})

const sentenceDB = 'sentences_db'
const sentences_db = couch.use(sentenceDB)
couch.db.create(sentenceDB, (err, body) => {
    if (err) {
        console.error(err, body)
    } else {
        console.log("Sentences Database created")
    }

    sentencesJs.sentences.forEach(sentence => {
        sentences_db.find({
            "selector": {
                "sentenceNumber": sentence.sentenceNumber
            }
        }, (err, resp) => {
            if (resp.docs.length > 0) {
                console.log("Sentence already exists")
                sentences_db.insert(sentence, {
                    "docName": resp.docs[0]._id,
                    "rev": resp.docs[0]._rev
                }, (err, body) => {
                    if (err) {
                        console.log("error inserting data", err)
                    } else {
                        console.log("Sentences inserted", body)
                    }
                })
                return
            }
            sentences_db.insert(sentence, (err, body) => {
                if (err) {
                    console.log("error inserting data", err)
                } else {
                    console.log("Sentences inserted", body)
                }
            })
        })
    })
})



app.get('/sentences', (req, res) => {
    sentences_db.find({ selector: { "sentenceNumber": { "$exists": true } } }, (err, resp) => {
        console.log(resp.docs)
        res.json(resp.docs)
    })
})


app.get('/sentences/random', (req, res) => {
   sentences_db.find({ selector: { "sentenceNumber": { "$exists": true } } }, (err, resp) => {
        const allSentences = resp.docs
        // .map(doc => doc.sentenceNumber).flat(1)
        const shuffledSentences = allSentences.sort(() => 0.5 - Math.random())
        const randomSentences = shuffledSentences.slice(0, 5)
        console.log(randomSentences)
        res.json(randomSentences)
        // console.log(resp.docs)
        // res.json(resp.docs)

    })
})
 

app.get('/categories', async (req, res) => {
    await questions_db.find({ selector: { "categoryName": { "$exists": true } }, fields: ["categoryName"] }, (err, resp) => {
        console.log(resp.docs.map(doc => doc.categoryName))
        res.json(resp.docs.map(doc => doc.categoryName))
    })
})

app.get('/categories/:categoryName/questions', async (req, res) => {
    const categoryName = req.params.categoryName
    await questions_db.find({ selector: { categoryName: categoryName }, fields: ["questions"] }, (err, resp) => {
        // console.log("aaa",resp)
        // console.log("bbb",resp.docs)
        // console.log("ccc",resp.docs[0].questions)
        // console.log("ddd", resp.docs[0].questions[0].answers)
        res.json(resp.docs[0].questions)
        console.log(JSON.stringify(resp.docs[0].questions[0].answers[0].image))
    })
})

app.get('/categories/:categoryName/questions/noun', async (req, res) => {
    const categoryName = req.params.categoryName
    await questions_db.find({ selector: { categoryName: categoryName }, fields: ["questions"] }, (err, resp) => {
       console.log(resp.docs[0].questions.map(question => question.questionLabel.split("'")[1]))
        res.json(resp.docs[0].questions.map(question => question.questionLabel.split("'")[1]))
        
    })
})


app.get('/categories/:categoryName', async (req, res) => {
    await questions_db.find({ selector: { categoryName: req.params.categoryName } }, (err, resp) => {
        // console.log(resp.docs[0].categoryName)
        res.json(resp.docs[0].categoryName)
    })
})



app.get('/questions/all', async (req, res) => {
    await questions_db.find({selector: { "questions": { "$exists":true} } , fields: ["questions"] },(err, resp) => {
        console.log((resp.docs.map(doc => doc.questions).flat(1)).sort(()=> 0.5 - Math.random()))
        res.json((resp.docs.map(doc => doc.questions).flat(1)).sort(()=> 0.5 - Math.random()))
    })
})

app.get('/questions/random', async (req, res) => {
    questions_db.find({selector: { "questions": { "$exists":true} } , fields: ["questions"] },(err, resp) => {
        const allQuestions = resp.docs.map(doc => doc.questions).flat(1)

        const shuffledQuestions = allQuestions.sort(() => 0.5 - Math.random())
        const randomQuestions = shuffledQuestions.slice(0, 10)
        console.log(randomQuestions)
        res.json(randomQuestions)

    })
})




app.listen(3000, () => {
    console.log("listening on 3000")
})
