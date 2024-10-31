import React from 'react';

const contacts = [
    { id: 1, name: 'Nguyen Huy', status: 'Active 31m ago' },
    { id: 2, name: 'Ngoc Hoan', status: 'Active 30m ago' },
];

const ContactList = () => {
    return (
        <div className="p-4">
            <input
                type="text"
                placeholder="Search (Ctrl+K)"
                className="w-full px-4 py-2 mb-4 text-black rounded-lg"
            />
            <ul className="space-y-3">
                {contacts.map((contact) => (
                    <li key={contact.id} className="flex items-center space-x-4 p-2 rounded-lg hover:bg-gray-800 cursor-pointer">
                        <img
                            src="https://via.placeholder.com/40"
                            alt="Avatar"
                            className="w-10 h-10 rounded-full"
                        />
                        <div>
                            <p className="font-bold">{contact.name}</p>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ContactList;
