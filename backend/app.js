const questions = require('./questions.json')
const sentencesJs = require('./sentences.json')
const sqlite3 = require('sqlite3').verbose();
const cors = require('cors')
const bcrypt = require('bcrypt')
const express = require('express')
const app = express()
const nano = require('nano')
// import questions from './questions.json'


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
                        res.json({ message: 'Registration successful' })
                    }

                });

                stmt.finalize()
            }
        }
    })

    // const stmt = db.prepare('INSERT INTO users (name, email, password, phone) VALUES (?, ?, ?, ?)');
    // stmt.run(name, email, hashedPassword, phone, function(err) {
    //     if (err) {
    //         console.error("Error during registration: ", err.message)
    //     } else {
    //         res.json({message: 'Registration successful'})
    //     }

    // });

    // stmt.finalize()


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
                    res.json({ message: 'Login successful', user: {name: row.name, email: row.email, phone: row.phone} })
                } else {
                    res.status(401).json({ message: 'Invalid password' })
                }
            } else {
                res.status(401).json({ message: 'User not found' })
            }
        }
    })
})

app.put('/profile', (req, res) => {
    const { email, name, phone } = req.body;

    db.run('UPDATE users SET name = ?, phone = ? WHERE email = ?', [name, phone, email], (err) => {
        if (err) {
            console.log(err)
        } else {
            res.json({ message: 'Profile updated' })
        }
    })
})

const fetch_ = require('node-fetch')

// async function getCookie() {
//     const response = await fetch_('http://localhost:5984/_session', {
//         method: 'post',
//         body: JSON.stringify({
//             username: 'andra',
//             password: 'andra'
//         }),
//         headers: { 'Content-Type': 'application/json' }
//     })
//     const cookie = response.headers.get('set-cookie')
//     console.log(cookie)
//     return cookie
// }


// const response = fetch_('http://localhost:5984/_session', {
//     method: 'post',
//     body: JSON.stringify({
//         username: 'andra',
//         password: 'andra'
//     }),
//     headers: { 'Content-Type': 'application/json' }
// })
// let cookie 
// console.log(cookie)
// response.then(res => {
//     cookie = res.headers.get('set-cookie').split('=')[1].split(';')[0]
//     // console.log(res.headers.get('set-cookie').split('=')[1].split(';')[0])
//     console.log(cookie)
// })

const couch = nano({
    url: 'http://andra:andra@localhost:5984',
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

// get sentence


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
