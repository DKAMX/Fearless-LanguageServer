package io.github.dkamx.fearless;

public class Sample {

  public static final String PROGRAM_HEADER = """
      package test
      alias base.Block as Block,
      alias base.Main as Main,
      alias base.Void as Void,
      alias base.caps.UnrestrictedIO as UnrestrictedIO,
      """;

  public static final String HELLO = PROGRAM_HEADER + """
      Test:Main {sys -> UnrestrictedIO#sys.println("Hello, World!")}
      """;

  public static final String HELLO_DEBUG = """
      package test
      Test:Main {sys -> base.Debug.println("Hello, World!")}
      """;

  public static final String HELLO_BLOCK1 = PROGRAM_HEADER + """
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .return {io.println("Hello, World!")}
        }
      """;

  public static final String HELLO_BLOCK2 = PROGRAM_HEADER + """
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .do {io.println("Hello, World!")}
        .return {Void}
        }
      """;

  public static final String HELLO_BLOCK3 = PROGRAM_HEADER + """
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .do {io.println("Hello, World!")}
        .done
        }
      """;

  public static final String FRUIT = """
      package test
      alias base.Str as Str,
      alias base.Nat as Nat,
      Fruit: {
        .name: Str,
        .value: Nat,
        }
      Apple: Fruit {
        .name -> "Apple",
        .value -> 1,
        }
      FruitMachine: {
        .detect(fruit: Fruit): Str -> fruit.name,
        .compare(fruit1: Fruit, fruit2: Fruit): Nat,
        }
      """;

  public static final String HELLO_ERR = """
      package test
      alias base.Main as Main,
      alias base.caps.UnrestrictedIO as , // error
      Test:Main {sys -> UnrestrictedIO#sys.println("Hello, World!")}
      """;

  public static final String LIST = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e}}
        .return {io.println("Hello, World!")}
      }
      """;

  public static final String LIST_CMPL = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> base.Id.id(e)}}
        .return {io.println("Hello, World!")}
      }
      """;

  public static final String LIST2 = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e.str}}
        .return {io.println("Hello, World!")}
      }
      """;

  public static final String LIST2_CMPL = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> base.Id.id(e.str)}}
        .return {io.println("Hello, World!")}
      }
      """;

  public static final String LIST_CMPL_BLOCK = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> base.Id.id(Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {data.flow.map{e -> e}})
        .return {io.println("Hello, World!")}
      }
      """;

  public static final String LIST_CMPL2 = PROGRAM_HEADER + """
      alias base.List as List,
      Test:Main {sys -> Block#
        .let io = {UnrestrictedIO#sys}
        .let data = {List#(1, 2, 3, 4)}
        .do {base.Id.id(data.flow.map{e -> e})}
        .return {io.println("Hello, World!")}
      }
      """;
}