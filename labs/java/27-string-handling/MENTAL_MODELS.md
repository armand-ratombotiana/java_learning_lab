# Mental Models for String Handling

## The Book Model (Immutability)
Think of a String as a printed book. You can read it, but you can't change it. To "change" it, you write a new book with the changes.

## The Pool Model (String Pool)
Think of the String pool as a library catalog. When you ask for a book (string literal), the librarian checks the catalog. If the book exists, you get the existing copy. If not, they create and catalog a new one.

## The Workshop Model (StringBuilder)
StringBuilder is a workshop where you build a string piece by piece. You add parts, insert, delete, and modify. When finished, you close the workshop and get the final product (toString()). StringBuffer is the same but with safety locks (synchronization).

## The Template Model (Text Blocks)
Text blocks are like pre-printed forms with blank spaces to fill in. The form maintains its multi-line structure while you fill in the dynamic parts.
