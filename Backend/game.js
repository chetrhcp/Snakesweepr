/*
  author gwphelps
  2/23/19
  is used by a game_handler that should probably exist at some point
  to update the stuff in the games.
*/
var BOARD_LENGTH = 10;
var BOARD_HEIGHT = 10;
var NUM_MINES = 7;
var SNAKE_LENGTH = 4


module.exports = class Game {

  constructor(player1, player2){
    this.snakeInit(player1);
    this.msInit(player2);
  }

  snakeInit(user){
    this._snake_state = {
      username: user,
      score: 0,
      food_arr: this.foodInit(),
      walls: this.wallsInit(),
      head: {x:SNAKE_LENGTH,y:BOARD_HEIGHT/2},
      snake_pos: this.bodyInit()
    };
  }

  wallsInit(){
    var walls = [];
    for(let i = 0; i < BOARD_HEIGHT; i++){
      walls.push({x:-1,y:i});
    }
    for(let i = 0; i < BOARD_LENGTH; i++){
      walls.push({x:i,y:0});
      walls.push({x:i,y:BOARD_HEIGHT});
    }
    for(let i = 0; i < BOARD_HEIGHT; i++){
      walls.push({x:BOARD_LENGTH,y:i});
    }
    return walls;
  }


  //GP- initializes the position of the snake
  bodyInit(){
    var body = [];
    for(var i = 0; i < SNAKE_LENGTH; i++){
      body.push({x:SNAKE_LENGTH-i,y:BOARD_HEIGHT/2});
    }

    return body;
  }

  //GP- creates all the fruit for the snake to eat, as well as the bombs for ms
  foodInit(){
    var fruit = [];
    for(let i = 0; i < NUM_MINES; i++){
      fruit.push({x:Math.floor(Math.random() * BOARD_LENGTH), y:Math.floor(Math.random() * BOARD_HEIGHT)});
    }
    return fruit;
  }

  //GP: initializes the minesweeper game
  msInit(user){
    this._ms_state = {
      username: user,
      bombs: this.foodInit(),//is functionally equivalent to getting the food
      uncovered_tiles: [],//fills up with points as the ms player uncovers tiles
      flagged_tile: [],//contains all the flagged coordinates
      score:0
    };
  }
//not to be confused with the inits called when the game is first made, this returns the initial state of the game
  gameInit(){

    var output_data = {
      snake:{
        snake_pos: this._snake_state.snake_pos,
        food_pos: this._snake_state.food_arr[0],
        walls: [],
        score: this._snake_state.score,
        died:false,
        won:false
      },
      minesweeper:{
        mine_pos: this._ms_state.bombs,
        score: this._ms_state.score,
        new_tiles:[],
        died:false,
        won:false
      }
    };

    return output_data;
  }


  //GP- returns the game data
  getData(){
    return JSON.stringify({snake: this._snake_state, minesweeper:this._ms_state});
  }



  //GP- gets the new state of one of the screens, and updates both player's screens accordingly.
  //returns the new game state as an object
  update(data){

    var output_data = {
      snake: {
        username: this._snake_state.username,
        food_pos: this._snake_state.food_arr[0],
        walls: [],
        snake_pos: [],
        score: this._snake_state.score,
        died: false,
        won: false
      },
      minesweeper: {
        username: this._ms_state.username,
        mine_pos: this._ms_state.bombs,
        new_tiles: [],
        flagged_tile:[],//for testing purposes only
        score:this._ms_state.score,
        died: false,
        won:false
      }
    };

    if(data.username == this._snake_state.username){
      this.updateSnake(output_data, data);
    }
    else if(data.username == this._ms_state.username){
      this.updateMS(output_data, data);
    }
    else{
      return "ERROR: data does not match any users in the game";
    }
    return output_data;
  }

  //GP- called when there is an update to the minesweeper game
  updateMS(output_data, data){

    var isBomb = false;
    var unflag = false;
    if(data.tile.x >= BOARD_LENGTH || data.tile.y >= BOARD_HEIGHT){
      return "ERROR: ms click exceeds board bounds";
    }
    if(data.tile.x < 0 || data.tile.y < 0){
      return "ERROR: ms click exceeds board bounds";
    }



    for(var i in this._ms_state.bombs){
      if(this._ms_state.bombs[i].x == data.tile.x && this._ms_state.bombs[i].y == data.tile.y){
        isBomb = true;
      }
    }


    if(data.click_type == 'flag'){
      var i = 0;

      while(true){

        if(this._ms_state.flagged_tile.length == 0 || i >= this._ms_state.flagged_tile.length){
          break;
        }

        var tempFlag = this._ms_state.flagged_tile.pop();

        if(tempFlag.x == data.tile.x && tempFlag.y == data.tile.y){
          unflag = true;
          break;
        }
        else{
          this._ms_state.flagged_tile.unshift(tempFlag);
        }
        i++;
      }

      if(isBomb && !unflag){
        this._ms_state.score += 1;

        output_data.snake.walls.push(this.addWall());
      }
      else{
        this._ms_state.score -= 1;
      }
      output_data.minesweeper.score = this._ms_state.score;
      if(!unflag){
        this._ms_state.flagged_tile.push(data.tile);
      }
    }
    else if(data.click_type == 'normal'){
      if(isBomb){

        output_data.minesweeper.died = true;
      }
      else{

        this._ms_state.uncovered_tiles.push(data.tile);
      }
    }

    output_data.minesweeper.score = this._ms_state.score;
    if (output_data.minesweeper.score == NUM_MINES){
      output_data.minesweeper.won = true;
    }
    output_data.minesweeper.flagged_tile = this._ms_state.flagged_tile;
    return output_data;
  }

  //GP: adds a wall to the snake game
  addWall(){
    var rdmPoint;
    var isFood;
    do{
      rdmPoint = {x:Math.floor(Math.random() * BOARD_LENGTH), y:Math.floor(Math.random() * BOARD_HEIGHT)};
      isFood = false;
      for(var i in this._snake_state.food_arr){
        if(this._snake_state.food_arr[i].x == rdmPoint.x && this._snake_state.food_arr.y == rdmPoint.y){
          isFood = true;
        }
      }
    }while(isFood);
    this._snake_state.walls.push(rdmPoint);
    return rdmPoint;
  }

  //GP- called when there is an update to the snake game
  updateSnake(output_data, data){

    var head = data.head_pos;

    //check for wall
    for(var i in this._snake_state.walls){
      if (head.x == this._snake_state.walls[i].x && head.y == this._snake_state.walls[i].y){
          output_data.snake.died = true;
      }
    }
    for(var i in this._snake_state.snake_pos){
      if (head.x == this._snake_state.snake_pos[i].x && head.y == this._snake_state.snake_pos[i].y){
          output_data.snake.died = true;
      }
    }

    //check for food
    if(head.x == this._snake_state.food_arr[0].x && head.y == this._snake_state.food_arr[0].y){

      this._snake_state.food_arr.shift();

      output_data.snake.food_pos = this._snake_state.food_arr[0];
      this._snake_state.score += 1;
      output_data.snake.score = this._snake_state.score;
      if(this._snake_state.food_arr.length == 0){
        output_data.snake.won = true;
      }
      output_data.minesweeper.new_tiles = this.unClickTiles();
    }

    this._snake_state.snake_pos.unshift(data.head_pos);
    this._snake_state.snake_pos.pop();

    output_data.snake.snake_pos = this._snake_state.snake_pos;

  }

//GP: unclicks the last 1/4 of the tiles, adds info to output
  unClickTiles(){
    var tiles = this._ms_state.uncovered_tiles;
    var new_tiles = [];
    for(var i = 0; i < tiles.length/2; i++){
      new_tiles.push(tiles.pop());
    }
    return new_tiles;
  }
}
