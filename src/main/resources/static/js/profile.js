document.addEventListener("DOMContentLoaded", function() {
    fetch('/profile.html')
        .then(response => {
            if (response.ok) return response.json();
            throw new Error('Failed to fetch profile data');
        })
        .then(data => {
            // Populate the user details
            document.getElementById('name').innerText = data.name;
            document.getElementById('email').innerText = data.email;
            document.getElementById('Total Fees').innerText = `$${data.totalFees}`;

            // Populate the loaned books table
            const loanedBooksTable = document.getElementById('loanedBooks');
            data.loanedBooks.forEach(book => {
                const row = document.createElement('tr');

                row.innerHTML = `
                        <td>${book.title}</td>
                        <td>${book.loanDate}</td>
                        <td>${book.dueDate}</td>
                        <td style="${book.overdue ? 'color: red;' : ''}">${book.status}</td>
                    `;
                loanedBooksTable.appendChild(row);
            });
        })
        .catch(error => console.error(error));
});